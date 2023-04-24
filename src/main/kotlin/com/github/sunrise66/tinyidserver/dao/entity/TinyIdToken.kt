package com.github.sunrise66.tinyidserver.dao.entity

import java.time.Instant
import jakarta.persistence.*
import java.sql.Timestamp

@Entity
@Table(name = "tiny_id_token")
open class TinyIdToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "INT UNSIGNED not null")
    open var id: Long? = null

    @Column(name = "token", nullable = false)
    open var token: String? = null

    @Column(name = "biz_type", nullable = false, length = 63)
    open var bizType: String? = null

    @Column(name = "remark", nullable = false)
    open var remark: String? = null

    @Column(name = "create_time", nullable = false)
    open var createTime: Timestamp? = null

    @Column(name = "update_time", nullable = false)
    open var updateTime: Timestamp? = null
}