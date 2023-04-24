package com.github.sunrise66.tinyidserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling

@EnableAsync
@EnableScheduling
@SpringBootApplication
class TinyidServerApplication

fun main(args: Array<String>) {
    runApplication<TinyidServerApplication>(*args)
}
