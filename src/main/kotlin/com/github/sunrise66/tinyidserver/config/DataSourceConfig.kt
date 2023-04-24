package com.github.sunrise66.tinyidserver.config

import me.sunrise.tinyid.server.config.DynamicDataSource
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.BeanUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.bind.Binder
import org.springframework.boot.context.properties.source.ConfigurationPropertySources
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.core.env.get
import org.springframework.util.PropertyPlaceholderHelper
import java.util.*
import javax.sql.DataSource

@Configuration
class DataSourceConfig(
    val enviroment: Environment
) {
    private final val logger: Logger = LoggerFactory.getLogger(javaClass)
    private final val SEP = ","
    private final val DEFAULT_DATASOURCE_TYPE = "org.apache.tomcat.jdbc.pool.DataSource"

    @Value("\${datasource.tinyid.names}")
    private lateinit var names: String

    @Value("\${datasource.tinyid.type}")
    private lateinit var dataSourceType: String


    @Bean
    fun getDynamicDataSource(): DataSource {
        val routingDataSource = DynamicDataSource()
        val dataSourceKeys = ArrayList<String>()
        val targetDataSources = HashMap<Any, Any>()
        routingDataSource.setTargetDataSources(targetDataSources)
        routingDataSource.setDataSourceKeys(dataSourceKeys)
        val sources = ConfigurationPropertySources.get(enviroment)
        val binder = Binder(sources)
        names.split(SEP).forEach { name ->
            val bindResult = binder.bind("datasource.tinyid.$name", Properties::class.java)
            val properties = bindResult.get()
            val dataSource = buildDataSource(dataSourceType, properties)
            targetDataSources[name] = dataSource
            dataSourceKeys.add(name)
        }
        return routingDataSource;
    }

    private fun buildDataSourceProperties(dataSource: DataSource, properties: Properties) {
        runCatching {
            // 此方法性能差，慎用
            BeanUtils.copyProperties(dataSource, properties)
        }.onFailure {
            logger.error("error copy properties", it)
        }
    }

    private fun buildDataSource(dataSourceType: String, properties: Properties): DataSource {
        return try {
            var className = DEFAULT_DATASOURCE_TYPE
            if ("" != dataSourceType.trim { it <= ' ' }) {
                className = dataSourceType
            }
            val type = Class.forName(className) as Class<out DataSource>
            val driverClassName = properties["driver-class-name"].toString()
            val placeholderHelper = PropertyPlaceholderHelper("\${", "}", ":", false)
            val url = placeholderHelper.replacePlaceholders(properties["url"].toString()) {
                enviroment[it]
            }
            val username = placeholderHelper.replacePlaceholders(properties["username"].toString()) {
                enviroment[it]
            }
            val password = placeholderHelper.replacePlaceholders(properties["password"].toString()) {
                enviroment[it]
            }
            DataSourceBuilder.create()
                .driverClassName(driverClassName)
                .url(url)
                .username(username)
                .password(password)
                .type(type)
                .build()
        } catch (e: ClassNotFoundException) {
            logger.error("buildDataSource error", e)
            throw IllegalStateException(e)
        }
    }

}