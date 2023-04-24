package com.github.sunrise66.tinyidserver.filter

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.annotation.WebFilter
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.boot.web.servlet.ServletComponentScan
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils

@Component
@ServletComponentScan
@WebFilter(urlPatterns = ["/*"], filterName = "requestFilter")
class RequestFilter : Filter {
    private final val logger = LoggerFactory.getLogger(javaClass)

    override fun doFilter(servletRequest: ServletRequest, servletResponse: ServletResponse, filterChain: FilterChain) {
        val request = servletRequest as HttpServletRequest
        val response = servletResponse as HttpServletResponse
        var params = ""
        val paramsMap = request.parameterMap
        if (paramsMap != null && paramsMap.isNotEmpty()) {
            for (entry in paramsMap.entries) {
                params += "${entry.key}:${StringUtils.arrayToDelimitedString(entry.value, ",")};"
            }
        }
        val start = System.currentTimeMillis()
        try {
            filterChain.doFilter(request, response)
        } catch (e: Throwable) {
            throw e
        } finally {
            val cost = System.currentTimeMillis() - start
            logger.info("request filter path={}, cost={}, params={}", request.servletPath, cost, params)
        }
    }
}