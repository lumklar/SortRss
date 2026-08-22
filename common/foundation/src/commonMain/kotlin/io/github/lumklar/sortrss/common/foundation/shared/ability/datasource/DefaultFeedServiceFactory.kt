package io.github.lumklar.sortrss.common.foundation.shared.ability.datasource

import io.github.lumklar.sortrss.common.domain.model.datasource.DataSourceConnectionDetails
import io.github.lumklar.sortrss.common.domain.shared.ability.datasource.FeedService
import io.github.lumklar.sortrss.common.domain.shared.ability.datasource.FeedServiceFactory
import io.github.lumklar.sortrss.common.domain.shared.enums.DataSourceType
import io.github.lumklar.sortrss.common.foundation.model.datasource.FeverApiConnectionDetails
import io.github.lumklar.sortrss.common.foundation.shared.ability.datasource.fever.FeverClient
import io.ktor.client.*

/**
 * 默认实现，支持 Fever API 和 Google Reader API（可按需扩展）。
 */
class DefaultFeedServiceFactory(
    private val httpClient: HttpClient? = null // 允许注入共享的 HttpClient，便于测试和复用
) : FeedServiceFactory {

    override fun create(connectionDetails: DataSourceConnectionDetails): FeedService {
        return when (connectionDetails.type) {
            DataSourceType.FEVER_API -> {
                val details = connectionDetails as? FeverApiConnectionDetails
                    ?: throw IllegalArgumentException("Fever API 连接详情类型错误")
                FeverClient(
                    baseUrl = details.endpoint.value,
                    username = details.username,
                    password = details.password,
                    httpClient = httpClient ?: defaultHttpClient()
                )
            }
            DataSourceType.GOOGLE_READER_API -> {
                TODO("Google Reader API 尚未实现")
            }
            DataSourceType.LOCAL_OPML -> {
                throw IllegalArgumentException("本地 OPML 数据源不支持通过 FeedService 访问")
            }
        }
    }

    private fun defaultHttpClient(): HttpClient = HttpClient {
        // 默认配置（可根据需要调整）
    }
}
