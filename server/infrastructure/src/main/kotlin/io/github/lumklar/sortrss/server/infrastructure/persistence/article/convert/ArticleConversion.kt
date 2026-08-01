package io.github.lumklar.sortrss.server.infrastructure.persistence.article.convert

import io.github.lumklar.sortrss.common.domain.model.article.Article
import io.github.lumklar.sortrss.common.domain.model.article.ArticleId
import io.github.lumklar.sortrss.server.infrastructure.persistence.article.entity.ArticlePO
import io.github.lumklar.sortrss.server.infrastructure.util.toJavaInstant
import io.github.lumklar.sortrss.server.infrastructure.util.toJavaInstantOrNull
import io.github.lumklar.sortrss.server.infrastructure.util.toKotlinInstant
import io.github.lumklar.sortrss.server.infrastructure.util.toKotlinInstantOrNull
import kotlin.uuid.toJavaUuid
import kotlin.uuid.toKotlinUuid

fun Article.toPO(): ArticlePO {
    return ArticlePO(
        id = this.id.value.toJavaUuid(),
        title = this.title,
        author = this.author,
        summary = this.summary,
        content = this.content,
        link = this.link,
        publishedAt = this.publishedAt.toJavaInstant(),
        guid = this.guid,
        contentModifiedTime = this.updatedAt.toJavaInstantOrNull(),
        gmtCreate = null,
        gmtModify = null
    )
}

fun ArticlePO.toDomain(): Article {
    val id = requireNotNull(this.id) { "ArticlePO.id must not be null" }
    val title = requireNotNull(this.title) { "ArticlePO.title must not be null" }
    val link = requireNotNull(this.link) { "ArticlePO.link must not be null" }
    val publishedAt = requireNotNull(this.publishedAt) { "ArticlePO.publishedAt must not be null" }

    return Article.reconstruct(
        id = ArticleId(id.toKotlinUuid()),
        title = title,
        author = this.author,
        summary = this.summary,
        content = this.content,
        link = link,
        publishedAt = publishedAt.toKotlinInstant(),
        updatedAt = this.contentModifiedTime.toKotlinInstantOrNull(),
        guid = this.guid
    )
}