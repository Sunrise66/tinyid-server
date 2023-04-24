package com.github.sunrise66.tinyidserver.service.impl

import com.github.sunrise66.tinyidserver.dao.entity.TinyIdToken
import com.github.sunrise66.tinyidserver.dao.repository.TinyIdTokenRepository
import com.github.sunrise66.tinyidserver.service.TinyIdTokenService
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class TinyIdTokenServiceImpl(val tinyIdTokenRepository: TinyIdTokenRepository) : TinyIdTokenService {
    private final val logger = LoggerFactory.getLogger(javaClass)
    private var token2bizTypes = HashMap<String, MutableSet<String>>()

    override fun canVisit(bizType: String?, token: String?): Boolean {
        if (null == bizType || null == token || bizType.isEmpty() || token.isEmpty()) {
            return false
        }
        val bizTypes = token2bizTypes[token]
        return bizTypes != null && bizTypes.contains(bizType)
    }

    /**
     * 1分钟刷新一次token
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    fun refresh() {
        logger.info("refresh token begin")
        init()
    }

    @PostConstruct
    private fun init() {
        synchronized(this) {
            logger.info("tinyId token init begin")
            val list = tinyIdTokenRepository.findAll()
            val map = convertToMap(list)
            token2bizTypes = map
            logger.info("tinyId token init success, token size:{}", list.size)
        }
    }

    fun convertToMap(list: List<TinyIdToken>?): HashMap<String, MutableSet<String>> {
        val map = HashMap<String, MutableSet<String>>(64)
        if (list != null) {
            for (tinyIdToken in list) {
                if (!map.containsKey(tinyIdToken.token)) {
                    map[tinyIdToken.token!!] = HashSet()
                }
                map[tinyIdToken.token]!!.add(tinyIdToken.bizType!!)
            }
        }
        return map
    }
}