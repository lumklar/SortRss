package io.github.lumklar.sortrss.server.infrastructure.persistence.feed.repository.jpa

import io.github.lumklar.sortrss.server.infrastructure.persistence.feed.entity.FeedArticlePk
import io.github.lumklar.sortrss.server.infrastructure.persistence.feed.entity.FeedArticlePo
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface FeedArticleJpaRepository : JpaRepository<FeedArticlePo, FeedArticlePk> {
    fun deleteById_FeedIdAndId_ArticleId(feedId: UUID, articleId: UUID)
    fun findById_FeedId(feedId: UUID): List<FeedArticlePo>
    fun deleteById_FeedId(feedId: UUID)
}
