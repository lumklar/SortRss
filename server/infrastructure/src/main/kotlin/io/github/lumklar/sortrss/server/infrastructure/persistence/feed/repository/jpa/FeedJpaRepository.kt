package io.github.lumklar.sortrss.server.infrastructure.persistence.feed.repository.jpa

import io.github.lumklar.sortrss.server.infrastructure.persistence.feed.entity.FeedPo
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface FeedJpaRepository : JpaRepository<FeedPo, UUID> {
    fun existsByFeedUrl(feedUrl: String): Boolean
}
