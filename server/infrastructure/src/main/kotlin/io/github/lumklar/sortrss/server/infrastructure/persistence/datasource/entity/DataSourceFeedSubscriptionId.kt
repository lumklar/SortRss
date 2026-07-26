package io.github.lumklar.sortrss.server.infrastructure.persistence.datasource.entity

import java.io.Serializable
import java.util.UUID

data class DataSourceFeedSubscriptionId(
    val dataSourceId: UUID,
    val feedId: UUID
) : Serializable