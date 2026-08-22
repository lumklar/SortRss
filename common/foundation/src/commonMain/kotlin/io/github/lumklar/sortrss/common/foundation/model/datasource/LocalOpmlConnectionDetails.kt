package io.github.lumklar.sortrss.common.foundation.model.datasource

import io.github.lumklar.sortrss.common.domain.model.datasource.DataSourceConnectionDetails
import io.github.lumklar.sortrss.common.domain.shared.enums.DataSourceType
import kotlin.uuid.Uuid

/**
 * 本地 OPML 连接详情
 * 无外部身份，identityKey 返回 null。
 */
data class LocalOpmlConnectionDetails(
    val detailsId: Uuid
) : DataSourceConnectionDetails {
    override val type: DataSourceType = DataSourceType.LOCAL_OPML
    override val identityKey: String = detailsId.toString()
}
