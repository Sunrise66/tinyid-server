package com.github.sunrise66.tinyidserver.service.impl

import com.github.sunrise66.tinyidserver.common.Constants
import com.github.sunrise66.tinyidserver.common.entity.SegmentId
import com.github.sunrise66.tinyidserver.dao.entity.TinyIdInfo
import com.github.sunrise66.tinyidserver.dao.repository.TinyIdInfoRepository
import com.github.sunrise66.tinyidserver.exception.TinyIdSysException
import com.github.sunrise66.tinyidserver.service.SegmentIdService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.atomic.AtomicLong

@Service
class DbSegmentIdServiceImpl(val tinyIdInfoRepository: TinyIdInfoRepository) : SegmentIdService {

    private final val logger = LoggerFactory.getLogger(javaClass)

    /**
     * Transactional标记保证query和update使用的是同一连接
     * 事务隔离级别应该为READ_COMMITTED,Spring默认是DEFAULT(取决于底层使用的数据库，mysql的默认隔离级别为REPEATABLE_READ)
     * <p>
     * 如果是REPEATABLE_READ，那么在本次事务中循环调用tinyIdInfoRepository.findByBizType(bizType)获取的结果是没有变化的，也就是查询不到别的事务提交的内容
     * 所以多次调用tinyIdInfoDAO.updateMaxId也就不会成功
     *
     * @param bizType
     * @return
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    override fun getNextSegmentId(bizType: String): SegmentId {
        for (i in 0 until Constants.RETRY) {
            val tinyIdInfo =
                tinyIdInfoRepository.findByBizType(bizType)?.get(0)
                    ?: throw TinyIdSysException("can not find biztype:$bizType")
            val newMaxId = tinyIdInfo.maxId!! + tinyIdInfo.step!!
            val newVersion = tinyIdInfo.version!! + 1
            tinyIdInfo.version = newVersion
            tinyIdInfo.maxId = newMaxId
            tinyIdInfoRepository.save(tinyIdInfo)
            val segmentId = convert(tinyIdInfo)
            logger.info(
                "getNextSegmentId success tinyIdInfo:{} current:{}",
                tinyIdInfo,
                segmentId
            )
            return segmentId

        }
        throw TinyIdSysException("get next segmentId conflict")
    }

    fun convert(idInfo: TinyIdInfo): SegmentId {
        val segmentId = SegmentId()
        segmentId.currentId = AtomicLong(idInfo.maxId!!.toLong() - idInfo.step!!)
        segmentId.maxId = idInfo.maxId!!
        segmentId.remainder = if (idInfo.remainder == null) 0 else idInfo.remainder!!
        segmentId.delta = if (idInfo.delta == null) 1 else idInfo.delta!!
        // 默认20%加载
        segmentId.loadingId = segmentId.currentId.get() + idInfo.step!! * Constants.LOADING_PERCENT / 100
        return segmentId
    }
}