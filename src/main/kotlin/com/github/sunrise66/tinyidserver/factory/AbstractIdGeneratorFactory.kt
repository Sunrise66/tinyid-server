package com.github.sunrise66.tinyidserver.factory

import com.github.sunrise66.tinyidserver.generator.IdGenerator
import java.util.concurrent.ConcurrentHashMap

abstract class AbstractIdGeneratorFactory : IdGeneratorFactory {
    private val generators = ConcurrentHashMap<String, IdGenerator>()

    override fun getIdGenerator(bizType: String): IdGenerator {
        if (generators.containsKey(bizType)) {
            return generators[bizType]!!
        }
        synchronized(this) {
            if (generators.containsKey(bizType)) {
                return generators[bizType]!!
            }
            val idGenerator = createIdGenerator(bizType)
            generators[bizType] = idGenerator
            return idGenerator
        }
    }

    abstract fun createIdGenerator(bizType: String): IdGenerator
}