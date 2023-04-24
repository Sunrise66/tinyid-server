package me.sunrise.tinyid.server.config

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource
import java.util.*

class DynamicDataSource : AbstractRoutingDataSource() {
    private lateinit var dataSourceKeys: List<String>

    override fun determineCurrentLookupKey(): Any {
        if (dataSourceKeys.size == 1) {
            return dataSourceKeys[0]
        }
        val r = Random()
        return dataSourceKeys[r.nextInt(dataSourceKeys.size)]
    }

    fun getDataSourceKeys(): List<String> {
        return dataSourceKeys
    }

    fun setDataSourceKeys(dataSourceKeys: List<String>) {
        this.dataSourceKeys = dataSourceKeys
    }

}