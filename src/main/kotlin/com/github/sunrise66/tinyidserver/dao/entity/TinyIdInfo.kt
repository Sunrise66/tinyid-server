package com.github.sunrise66.tinyidserver.dao.entity

import jakarta.persistence.*
import java.sql.Timestamp
import java.time.Instant

@Entity
@Table(name = "tiny_id_info")
open class TinyIdInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    open var id: Long? = null

    @Column(name = "biz_type", nullable = false, length = 63)
    open var bizType: String? = null

    @Column(name = "begin_id", nullable = false)
    open var beginId: Long? = null

    @Column(name = "max_id", nullable = false)
    open var maxId: Long? = null

    @Column(name = "step")
    open var step: Int? = null

    @Column(name = "delta", nullable = false)
    open var delta: Int? = null

    @Column(name = "remainder", nullable = false)
    open var remainder: Int? = null

    @Column(name = "create_time", nullable = false)
    open var createTime: Timestamp? = null

    @Column(name = "update_time", nullable = false)
    open var updateTime: Timestamp? = null

    @Column(name = "version", nullable = false)
    open var version: Long? = null
}