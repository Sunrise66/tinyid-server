package com.github.sunrise66.tinyidserver.dao.repository

import com.github.sunrise66.tinyidserver.dao.entity.TinyIdInfo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface TinyIdInfoRepository : JpaRepository<TinyIdInfo, Long>, JpaSpecificationExecutor<TinyIdInfo> {
    /**
     * 根据bizType获取db中的tinyId对象
     * @param bizType
     * @return
     */
    fun findByBizType(bizType: String): List<TinyIdInfo>?

    /**
     * 根据id、oldMaxId、version、bizType更新最新的maxId
     * @param id
     * @param newMaxId
     * @param oldMaxId
     * @param version
     * @param bizType
     * @return
     */
    @Modifying
    @Query(
        value = """
        update TinyIdInfo t set t.maxId = :newMaxId ,
        t.updateTime = current_timestamp ,
        t.version = t.version + 1
        where t.id = :id
        and t.maxId = :oldMaxId
        and t.version = :version
        and t.bizType = :bizType
    """
    )
    fun updateMaxId(id: Long, newMaxId: Long, oldMaxId: Long, version: Long, bizType: String): Int

    fun findByIdAndMaxIdAndVersionAndBizType(id: Long, maxId: Long, version: Long, bizType: String): TinyIdInfo?
}