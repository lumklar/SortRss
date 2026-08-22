package io.github.lumklar.sortrss.common.foundation.model.datasource

import io.github.lumklar.sortrss.common.domain.model.datasource.DataSourceConnectionDetails
import io.github.lumklar.sortrss.common.domain.shared.enums.DataSourceType
import io.github.lumklar.sortrss.common.domain.shared.valueobjects.Url

/**
 * Fever API 连接详情
 */
data class FeverApiConnectionDetails(
    val endpoint: Url,
    val username: String,
    val password: String
) : DataSourceConnectionDetails {
    override val type: DataSourceType = DataSourceType.FEVER_API

    override val identityKey: String =
        "fever:${endpoint.value.trimEnd('/')}:$username"

    init {
        require(username.isNotBlank()) { "Fever username cannot be blank" }
        require(password.isNotBlank()) { "Fever API key cannot be blank" }
    }
}
