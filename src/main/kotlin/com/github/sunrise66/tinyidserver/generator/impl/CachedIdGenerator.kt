package com.github.sunrise66.tinyidserver.generator.impl

import com.github.sunrise66.tinyidserver.common.entity.ResultCode
import com.github.sunrise66.tinyidserver.common.entity.SegmentId
import com.github.sunrise66.tinyidserver.exception.TinyIdSysException
import com.github.sunrise66.tinyidserver.generator.IdGenerator
import com.github.sunrise66.tinyidserver.service.SegmentIdService
import com.github.sunrise66.tinyidserver.util.NamedThreadFactory
import java.util.concurrent.Executors

class CachedIdGenerator(
        val bizType: String,
        val segmentIdService: SegmentIdService
) : IdGenerator {

    private val lock = Any()

    @Volatile
    private var current: SegmentId? = null

    @Volatile
    private var next: SegmentId? = null

    @Volatile
    private var isLoadingNext = false
    private val executorService = Executors.newSingleThreadExecutor(NamedThreadFactory("tinyid-generator"))

    init {
        loadCurrent()
    }

    @Synchronized
    fun loadCurrent() {
        current.takeIf {
            it == null || !it.useful()
        }.let {
            if (next == null) {
                current = querySegmentId()
            } else {
                current = next
                next = null
            }
        }
    }

    private fun querySegmentId(): SegmentId? {
        var message: String? = null
        runCatching {
            return segmentIdService.getNextSegmentId(bizType)
        }.onFailure {
            message = it.message
        }
        throw TinyIdSysException("error query segmentId: $message")
    }

    fun loadNext() {
        if (next == null && !isLoadingNext) {
            synchronized(lock) {
                if (next == null && !isLoadingNext) {
                    isLoadingNext = true
                    executorService.submit {
                        next = try {
                            // 无论获取下个segmentId成功与否，都要将isLoadingNext赋值为false
                            querySegmentId()
                        } finally {
                            isLoadingNext = false
                        }
                    }
                }
            }
        }
    }

    override fun nextId(): Long {
        while (true) {
            if (current == null) {
                loadCurrent()
                continue
            }
            val result = current!!.nextId()
            if (result.code == ResultCode.OVER.code) {
                loadCurrent()
            } else {
                if (result.code == ResultCode.LOADING.code) {
                    loadNext()
                }
                return result.id
            }
        }
    }

    override fun nextId(batchSize: Int): List<Long> {
        val ids: MutableList<Long> = ArrayList()
        for (i in 0 until batchSize) {
            val id = nextId()
            ids.add(id)
        }
        return ids
    }
}