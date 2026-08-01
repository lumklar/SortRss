package io.github.lumklar.sortrss.common.domain.model.feed

import io.github.lumklar.sortrss.common.domain.model.article.ArticleId

interface FeedRepository {
    /**
     * 保存订阅源（新建或更新）。
     * @return 包含最新状态的 Feed（如更新后的 lastSyncTime）
     */
    fun save(feed: Feed): Feed

    /**
     * 根据 ID 查询订阅源。
     */
    fun findById(id: FeedId): Feed?

    /**
     * 删除指定订阅源。
     */
    fun delete(feed: Feed)

    /**
     * 检查指定 feedUrl 是否已存在（用于去重）。
     */
    fun existsByFeedUrl(feedUrl: String): Boolean

    // ---- 文章关联管理 (FeedArticle) ----

    /**
     * 添加文章与订阅源的关联。
     */
    fun addArticle(feedArticle: FeedArticle)

    /**
     * 移除文章与订阅源的关联。
     */
    fun removeArticle(feedArticle: FeedArticle)

    /**
     * 查询指定订阅源下的所有文章 ID。
     */
    fun findArticleIdsByFeedId(feedId: FeedId): List<ArticleId>
}
