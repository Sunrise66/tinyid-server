package com.github.sunrise66.tinyidserver.util

import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

class NamedThreadFactory(
        val namePrefix: String,
        val daemon: Boolean = false
) : ThreadFactory {

    private val group = Thread.currentThread().threadGroup
    private val threadNumber = AtomicInteger(1)

    override fun newThread(r: Runnable): Thread {
        val t = Thread(group, r, "$namePrefix-thread-${threadNumber.getAndIncrement()}", 0)
        t.isDaemon = daemon
        return t
    }
}