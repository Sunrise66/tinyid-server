package com.github.sunrise66.tinyidserver.service

import com.github.sunrise66.tinyidserver.common.entity.SegmentId

interface SegmentIdService {
    /**
     * 根据bizType获取下一个SegmentId对象
     * @param bizType
     * @return
     */
    fun getNextSegmentId(bizType: String): SegmentId
}