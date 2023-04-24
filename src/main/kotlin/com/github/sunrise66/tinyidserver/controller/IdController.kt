package com.github.sunrise66.tinyidserver.controller

import com.github.sunrise66.tinyidserver.common.entity.SegmentId
import com.github.sunrise66.tinyidserver.factory.IdGeneratorFactory
import com.github.sunrise66.tinyidserver.service.SegmentIdService
import com.github.sunrise66.tinyidserver.service.TinyIdTokenService
import com.github.sunrise66.tinyidserver.vo.ErrorCode
import com.github.sunrise66.tinyidserver.vo.Response
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/tinyid/id/")
class IdController(
    val idGeneratorFactory: IdGeneratorFactory,
    val segmentIdService: SegmentIdService,
    val tinyIdTokenService: TinyIdTokenService
) {
    private final val logger = LoggerFactory.getLogger(javaClass)

    @Value("\${batch.size.max:100000}")
    private var batchSizeMax: Int = 100000

    @PostMapping("nextId")
    fun nextId(
        @RequestParam bizType: String,
        @RequestParam batchSize: Int,
        @RequestParam token: String
    ): Response<List<Long>?> {
        val newBatchSize = checkBatchSize(batchSize)
        if (!tinyIdTokenService.canVisit(bizType, token)) {
            return Response(null, ErrorCode.TOKEN_ERR.code, ErrorCode.TOKEN_ERR.message)
        }
        return try {
            val idGenerator = idGeneratorFactory.getIdGenerator(bizType)
            val ids = idGenerator.nextId(newBatchSize)
            Response(ids)
        } catch (e: Exception) {
            logger.error("nextId error", e)
            Response(null, ErrorCode.SYS_ERR.code, ErrorCode.SYS_ERR.message)
        }
    }

    @PostMapping("nextIdSimple")
    fun nextIdSimple(
        @RequestParam bizType: String,
        @RequestParam batchSize: Int,
        @RequestParam token: String
    ): String {
        val newBatchSize = checkBatchSize(batchSize)
        if (!tinyIdTokenService.canVisit(bizType, token)) {
            return ""
        }
        var response = ""
        try {
            val idGenerator = idGeneratorFactory.getIdGenerator(bizType)
            response = if (newBatchSize == 1) {
                val id = idGenerator.nextId()
                id.toString()
            } else {
                val idList: List<Long> = idGenerator.nextId(newBatchSize)
                val sb = StringBuilder()
                for (id in idList) {
                    sb.append(id).append(",")
                }
                sb.deleteCharAt(sb.length - 1).toString()
            }
        } catch (e: Exception) {
            logger.error("nextIdSimple error", e)
        }
        return response
    }

    @PostMapping("nextSegmentId")
    fun nextSegmentId(
        @RequestParam bizType: String,
        @RequestParam token: String
    ): Response<SegmentId?> {
        if (!tinyIdTokenService.canVisit(bizType, token)) {
            return Response(null, ErrorCode.TOKEN_ERR.code, ErrorCode.TOKEN_ERR.message)
        }
        return try {
            val segmentId: SegmentId = segmentIdService.getNextSegmentId(bizType)
            Response(segmentId)
        } catch (e: Exception) {
            logger.error("nextSegmentId error", e)
            Response(null, ErrorCode.SYS_ERR.code, ErrorCode.SYS_ERR.message)
        }
    }

    @PostMapping("nextSegmentIdSimple")
    fun nextSegmentIdSimple(
        @RequestParam bizType: String,
        @RequestParam token: String
    ): String? {
        if (!tinyIdTokenService.canVisit(bizType, token)) {
            return ""
        }
        var response = ""
        try {
            val segmentId: SegmentId = segmentIdService.getNextSegmentId(bizType)
            response =
                "${segmentId.currentId},${segmentId.loadingId},${segmentId.maxId},${segmentId.delta},${segmentId.remainder}"
        } catch (e: Exception) {
            logger.error("nextSegmentIdSimple error", e)
        }
        return response
    }

    private fun checkBatchSize(batchSize: Int?): Int {
        var batchSize = batchSize
        if (batchSize == null) {
            batchSize = 1
        }
        if (batchSize > batchSizeMax) {
            batchSize = batchSizeMax
        }
        return batchSize
    }
}