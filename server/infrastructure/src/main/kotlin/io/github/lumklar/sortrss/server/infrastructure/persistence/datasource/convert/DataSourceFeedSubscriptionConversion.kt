package io.github.lumklar.sortrss.server.infrastructure.persistence.datasource.convert

import io.github.lumklar.sortrss.common.domain.model.datasource.DataSourceFeedSubscription
import io.github.lumklar.sortrss.common.domain.model.datasource.DataSourceId
import io.github.lumklar.sortrss.common.domain.model.feed.FeedId
import io.github.lumklar.sortrss.server.infrastructure.persistence.datasource.entity.DataSourceFeedSubscriptionPo
import io.github.lumklar.sortrss.server.infrastructure.util.toJavaInstantOrNull
import io.github.lumklar.sortrss.server.infrastructure.util.toKotlinInstantOrNull
import kotlin.uuid.toJavaUuid
import kotlin.uuid.toKotlinUuid

fun DataSourceFeedSubscription.toPo(): DataSourceFeedSubscriptionPo =
    DataSourceFeedSubscriptionPo().apply {
        dataSourceId = this@toPo.dataSourceId.value.toJavaUuid()
        feedId = this@toPo.feedId.value.toJavaUuid()
        customTitle = this@toPo.customTitle
        remoteId = this@toPo.remoteId
        lastAllReadAt = this@toPo.lastAllReadAt.toJavaInstantOrNull()
    }

fun DataSourceFeedSubscriptionPo.toDomain(): DataSourceFeedSubscription =
    DataSourceFeedSubscription.create(
        dataSourceId = DataSourceId(this.dataSourceId!!.toKotlinUuid()),
        feedId = FeedId(this.feedId!!.toKotlinUuid()),
        remoteId = this.remoteId,
        customTitle = this.customTitle,
        lastAllReadAt = this.lastAllReadAt.toKotlinInstantOrNull()
    )
