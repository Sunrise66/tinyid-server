package com.github.sunrise66.tinyidserver.generator

interface IdGenerator {
    fun nextId(): Long
    fun nextId(batchSize: Int): List<Long>
}