package io.github.lumklar.sortrss.common.domain.model.feed

import io.github.lumklar.sortrss.common.domain.model.article.ArticleId

/**
 * 订阅源（Feed）与文章（Article）之间的关联对象。
 * 表示某篇文章属于某个订阅源，可以携带文章在订阅源中的唯一标识、顺序等元信息。
 */
data class FeedArticle(
    val feedId: FeedId,
    val articleId: ArticleId,
    // 业务字段：该文章在数据源中所属的分组ID
    val sourceArticleId: String? = null
)