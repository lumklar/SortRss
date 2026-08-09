package io.github.lumklar.sortrss.server.infrastructure.persistence.datasource.convert

import io.github.lumklar.sortrss.common.domain.model.datasource.DataSourceFeedSubscription
import io.github.lumklar.sortrss.common.domain.model.datasource.DataSourceId
import io.github.lumklar.sortrss.common.domain.model.feed.FeedId
import io.github.lumklar.sortrss.common.foundation.validation.password.UUIDGenerator
import io.github.lumklar.sortrss.server.infrastructure.persistence.datasource.entity.DataSourceFeedSubscriptionPo
import io.github.lumklar.sortrss.server.infrastructure.util.toJavaInstantOrNull
import io.github.lumklar.sortrss.server.infrastructure.util.toKotlinInstantOrNull
import kotlin.uuid.toJavaUuid
import kotlin.uuid.toKotlinUuid

fun DataSourceFeedSubscription.toPO(): DataSourceFeedSubscriptionPo {
    return DataSourceFeedSubscriptionPo(
        id = UUIDGenerator.generateUuid().toJavaUuid(),
        dataSourceId = this.dataSourceId.value.toJavaUuid(),
        feedId = this.feedId.value.toJavaUuid(),
        customTitle = this.customTitle,
        remoteId = this.remoteId,
        lastAllReadAt = this.lastAllReadAt.toJavaInstantOrNull(),
        gmtCreate = null,
        gmtModify = null
    )
}

fun DataSourceFeedSubscriptionPo.toDomain(): DataSourceFeedSubscription {
    val dataSourceId = requireNotNull(this.dataSourceId) { "DataSourceFeedSubscriptionPo.dataSourceId must not be null" }
    val feedId = requireNotNull(this.feedId) { "DataSourceFeedSubscriptionPo.feedId must not be null" }

    return DataSourceFeedSubscription.create(
        dataSourceId = DataSourceId(dataSourceId.toKotlinUuid()),
        feedId = FeedId(feedId.toKotlinUuid()),
        remoteId = this.remoteId,
        customTitle = this.customTitle,
        lastAllReadAt = this.lastAllReadAt.toKotlinInstantOrNull()
    )
}