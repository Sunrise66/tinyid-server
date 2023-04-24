package com.github.sunrise66.tinyidserver.factory.impl

import com.github.sunrise66.tinyidserver.factory.AbstractIdGeneratorFactory
import com.github.sunrise66.tinyidserver.generator.IdGenerator
import com.github.sunrise66.tinyidserver.generator.impl.CachedIdGenerator
import com.github.sunrise66.tinyidserver.service.SegmentIdService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class IdGeneratorFactoryServer(val tinyIdService: SegmentIdService) : AbstractIdGeneratorFactory() {
    private final var logger = LoggerFactory.getLogger(javaClass)

    override fun createIdGenerator(bizType: String): IdGenerator {
        logger.info("createIdGenerator :{}", bizType)
        return CachedIdGenerator(bizType, tinyIdService)
    }


}