package com.github.sunrise66.tinyidserver.vo

data class Response<T>(val data: T, val code: Int = 200, val message: String = "")