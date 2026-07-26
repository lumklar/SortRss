package io.github.lumklar.sortrss.common.domain.model.feed

import io.github.lumklar.sortrss.common.domain.model.article.ArticleId

/**
 * 订阅源（Feed）与文章（Article）之间的关联对象。
 * 表示某篇文章属于某个订阅源，可以携带文章在订阅源中的唯一标识、顺序等元信息。
 */
data class FeedArticle(
    val feedId: FeedId,
    val articleId: ArticleId,
    val guid: String? = null,           // 文章在该订阅源中的唯一标识符
    val sortOrder: Int = 0              // 该文章在订阅源中的排序权重（可选）
)