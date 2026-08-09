package io.github.lumklar.sortrss.server.infrastructure.persistence.feed.convert

import io.github.lumklar.sortrss.common.domain.model.article.ArticleId
import io.github.lumklar.sortrss.common.domain.model.feed.FeedArticle
import io.github.lumklar.sortrss.common.domain.model.feed.FeedId
import io.github.lumklar.sortrss.server.infrastructure.persistence.feed.entity.FeedArticlePk
import io.github.lumklar.sortrss.server.infrastructure.persistence.feed.entity.FeedArticlePo
import kotlin.uuid.toJavaUuid
import kotlin.uuid.toKotlinUuid

fun FeedArticle.toPO(): FeedArticlePo {
    return FeedArticlePo(
        id = FeedArticlePk(
            feedId = this.feedId.value.toJavaUuid(),
            articleId = this.articleId.value.toJavaUuid()
        ),
        sourceArticleId = this.sourceArticleId,   // 映射新字段
        gmtCreate = null,
        gmtModify = null
    )
}

fun FeedArticlePo.toDomain(): FeedArticle {
    val pk = requireNotNull(this.id) { "FeedArticlePo.id must not be null" }
    return FeedArticle(
        feedId = FeedId(pk.feedId.toKotlinUuid()),
        articleId = ArticleId(pk.articleId.toKotlinUuid()),
        sourceArticleId = this.sourceArticleId    // 映射新字段
    )
}