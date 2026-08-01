package io.github.lumklar.sortrss.server.infrastructure.persistence.feed.convert

import io.github.lumklar.sortrss.common.domain.model.feed.Feed
import io.github.lumklar.sortrss.common.domain.model.feed.FeedId
import io.github.lumklar.sortrss.server.infrastructure.persistence.feed.entity.FeedPo
import io.github.lumklar.sortrss.server.infrastructure.util.toJavaInstantOrNull
import io.github.lumklar.sortrss.server.infrastructure.util.toKotlinInstantOrNull
import kotlin.uuid.toJavaUuid
import kotlin.uuid.toKotlinUuid

fun FeedPo.toDomain(): Feed {
    val id = requireNotNull(this.id) { "FeedPo.id must not be null" }
    val feedUrl = requireNotNull(this.feedUrl) { "FeedPo.feedUrl must not be null" }
    val title = requireNotNull(this.title) { "FeedPo.title must not be null" }
    val sourceType = requireNotNull(this.sourceType) { "FeedPo.sourceType must not be null" }

    return Feed.reconstruct(
        id = FeedId(id.toKotlinUuid()),
        feedUrl = feedUrl,
        title = title,
        siteUrl = this.siteUrl,
        description = this.description,
        iconUrl = this.iconUrl,
        sourceType = sourceType,
        initialLastSyncTime = this.lastSyncTime.toKotlinInstantOrNull()
    )
}

fun Feed.toPO(): FeedPo {
    return FeedPo(
        id = this.id.value.toJavaUuid(),
        feedUrl = this.feedUrl,
        title = this.title,
        siteUrl = this.siteUrl,
        description = this.description,
        iconUrl = this.iconUrl,
        sourceType = this.sourceType,
        lastSyncTime = this.lastSyncTime.toJavaInstantOrNull(),
        gmtCreate = null,
        gmtModify = null
    )
}