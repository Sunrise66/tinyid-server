package com.github.sunrise66.tinyidserver.service

interface TinyIdTokenService {
    /**
     * 是否有权限
     * @param bizType
     * @param token
     * @return
     */
    fun canVisit(bizType: String?, token: String?): Boolean
}