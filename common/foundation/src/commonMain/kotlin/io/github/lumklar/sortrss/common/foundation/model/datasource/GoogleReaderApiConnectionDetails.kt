package io.github.lumklar.sortrss.common.foundation.model.datasource

import io.github.lumklar.sortrss.common.domain.model.datasource.DataSourceConnectionDetails
import io.github.lumklar.sortrss.common.domain.shared.enums.DataSourceType
import io.github.lumklar.sortrss.common.domain.shared.valueobjects.Url

/**
 * Google Reader API 连接详情
 */
data class GoogleReaderApiConnectionDetails(
    val endpoint: Url,
    val accessToken: String
) : DataSourceConnectionDetails {
    override val type: DataSourceType = DataSourceType.GOOGLE_READER_API

    override val identityKey: String =
        "google-reader:${endpoint.value.trimEnd('/')}:$accessToken"

    init {
        require(accessToken.isNotBlank()) { "Access token cannot be blank" }
    }
}
