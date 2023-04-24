package com.github.sunrise66.tinyidserver.common.entity

import java.util.concurrent.atomic.AtomicLong

class SegmentId {
    var maxId: Long = 0
    var loadingId: Long = 0
    lateinit var currentId: AtomicLong

    /**
     * increment by
     */
    var delta = 0

    /**
     * mod num
     */
    var remainder = 0

    @Volatile
    var isInit = false

    private fun init() {
        if (isInit) {
            return
        }
        synchronized(this) {
            if (isInit) {
                return
            }
            var id = currentId.get()
            if (id % delta == remainder.toLong()) {
                isInit = true
                return
            }
            for (i in 0..delta) {
                id = currentId.incrementAndGet()
                if (id % delta == remainder.toLong()) {
                    // 避免浪费 减掉系统自己占用的一个id
                    currentId.addAndGet((0 - delta).toLong())
                    isInit = true
                    return
                }
            }
        }
    }

    fun nextId(): Result {
        init()
        val id = currentId.addAndGet(delta.toLong())
        if (id > maxId) {
            return Result(ResultCode.OVER.code, id)
        }
        return if (id >= loadingId) {
            Result(ResultCode.LOADING.code, id)
        } else Result(ResultCode.NORMAL.code, id)
    }

    fun useful(): Boolean {
        return currentId.get() <= maxId
    }

    override fun toString(): String {
        return "[maxId=$maxId,loadingId=$loadingId,currentId=$currentId,delta=$delta,remainder=$remainder]"
    }
}