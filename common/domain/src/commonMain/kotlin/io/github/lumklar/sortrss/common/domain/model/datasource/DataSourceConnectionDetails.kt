package io.github.lumklar.sortrss.common.domain.model.datasource

import io.github.lumklar.sortrss.common.domain.shared.valueobjects.Url

/**
 * 数据源连接详情（值对象）。
 * 根据不同的数据源类型，持有不同的连接参数。
 */
sealed class DataSourceConnectionDetails {

    /** 本地 OPML 导入源，无需任何连接参数 */
    data object LocalOpml : DataSourceConnectionDetails()

    /** Fever API 兼容源 */
    data class FeverApi(
        val endpoint: Url,   // 例如：https://feeds.example.com
        val username: String,
        val apiKey: String
    ) : DataSourceConnectionDetails() {
        init {
            require(username.isNotBlank()) { "Fever username cannot be blank" }
            require(apiKey.isNotBlank()) { "Fever API key cannot be blank" }
        }
    }

    /** Google Reader API 兼容源 */
    data class GoogleReaderApi(
        val endpoint: Url,
        val accessToken: String
    ) : DataSourceConnectionDetails() {
        init {
            require(accessToken.isNotBlank()) { "Access token cannot be blank" }
        }
    }
}
