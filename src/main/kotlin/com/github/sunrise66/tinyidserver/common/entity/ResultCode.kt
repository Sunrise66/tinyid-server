package com.github.sunrise66.tinyidserver.common.entity

enum class ResultCode(val code: Int) {
    /**
     * 正常可用
     */
    NORMAL(1),

    /**
     *需要去加载nextId
     */
    LOADING(2),

    /**
     *超过maxId 不可用
     */
    OVER(3)
}