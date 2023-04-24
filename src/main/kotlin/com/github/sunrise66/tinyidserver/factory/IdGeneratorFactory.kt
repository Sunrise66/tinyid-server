package com.github.sunrise66.tinyidserver.factory

import com.github.sunrise66.tinyidserver.generator.IdGenerator

interface IdGeneratorFactory {
    /**
     * 根据bizType创建id生成器
     * @param bizType
     * @return
     */
    fun getIdGenerator(bizType: String): IdGenerator
}