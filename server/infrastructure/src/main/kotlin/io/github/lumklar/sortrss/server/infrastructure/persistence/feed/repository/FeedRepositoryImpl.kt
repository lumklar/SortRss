package io.github.lumklar.sortrss.server.infrastructure.persistence.feed.repository

import io.github.lumklar.sortrss.common.domain.model.article.ArticleId
import io.github.lumklar.sortrss.common.domain.model.feed.Feed
import io.github.lumklar.sortrss.common.domain.model.feed.FeedArticle
import io.github.lumklar.sortrss.common.domain.model.feed.FeedId
import io.github.lumklar.sortrss.common.domain.model.feed.FeedRepository
import io.github.lumklar.sortrss.server.infrastructure.persistence.feed.convert.toDomain
import io.github.lumklar.sortrss.server.infrastructure.persistence.feed.convert.toPO
import io.github.lumklar.sortrss.server.infrastructure.persistence.feed.repository.jpa.FeedArticleJpaRepository
import io.github.lumklar.sortrss.server.infrastructure.persistence.feed.repository.jpa.FeedJpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import kotlin.uuid.toJavaUuid

@Repository
class FeedRepositoryImpl(
    private val feedJpaRepository: FeedJpaRepository,
    private val feedArticleJpaRepository: FeedArticleJpaRepository
) : FeedRepository {

    @Transactional
    override fun save(feed: Feed): Feed {
        val po = feed.toPO()
        val savedPo = feedJpaRepository.save(po)
        return savedPo.toDomain()
    }

    override fun findById(id: FeedId): Feed? {
        val po = feedJpaRepository.findById(id.value.toJavaUuid()).orElse(null) ?: return null
        return po.toDomain()
    }

    @Transactional
    override fun delete(feed: Feed) {
        // 先删除所有关联的文章记录
        feedArticleJpaRepository.deleteById_FeedId(feed.id.value.toJavaUuid())
        feedJpaRepository.delete(feed.toPO())
    }

    override fun existsByFeedUrl(feedUrl: String): Boolean {
        return feedJpaRepository.existsByFeedUrl(feedUrl)
    }

    @Transactional
    override fun addArticle(feedArticle: FeedArticle) {
        val po = feedArticle.toPO()
        feedArticleJpaRepository.save(po)
    }

    @Transactional
    override fun removeArticle(feedArticle: FeedArticle) {
        feedArticleJpaRepository.deleteById_FeedIdAndId_ArticleId(
            feedArticle.feedId.value.toJavaUuid(),
            feedArticle.articleId.value.toJavaUuid()
        )
    }

    override fun findArticleIdsByFeedId(feedId: FeedId): List<ArticleId> {
        val uuid = feedId.value.toJavaUuid()
        return findArticleIds(uuid)
    }

    /**
     * 根据数据库 feed UUID 查询关联的文章 ID 列表
     */
    private fun findArticleIds(feedUuid: java.util.UUID): List<ArticleId> {
        return feedArticleJpaRepository.findById_FeedId(feedUuid)
            .map { it.toDomain().articleId }
    }
}
