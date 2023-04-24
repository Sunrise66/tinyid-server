package com.github.sunrise66.tinyidserver.vo

enum class ErrorCode(val code: Int,val message: String) {
    TOKEN_ERR(5, "token is error"),
    SYS_ERR(6, "sys error")
}