package io.github.lumklar.sortrss.common.domain.model.datasource

import kotlin.jvm.JvmInline

@JvmInline
value class DataSourceName private constructor(val value: String) {
    init {
        require(value.isNotBlank()) { "数据源名称不能为空" }
        require(value.length <= 50) { "数据源名称不能超过50个字符" }
    }

    companion object {
        fun fromString(raw: String): DataSourceName = DataSourceName(raw.trim())
    }
}
