package io.github.lumklar.sortrss.server.infrastructure.persistence.feed.entity

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.io.Serializable
import java.util.*

@Embeddable
data class FeedArticlePk(
    @Column(name = "feed_id", nullable = false)
    val feedId: UUID,

    @Column(name = "article_id", nullable = false)
    val articleId: UUID
) : Serializable