package io.github.lumklar.sortrss.server.infrastructure.persistence.article.convert

import io.github.lumklar.sortrss.common.domain.model.article.ArticleId
import io.github.lumklar.sortrss.common.domain.model.article.UserArticleState
import io.github.lumklar.sortrss.common.domain.model.user.UserId
import io.github.lumklar.sortrss.server.infrastructure.persistence.article.entity.UserArticleStateId
import io.github.lumklar.sortrss.server.infrastructure.persistence.article.entity.UserArticleStatePO
import kotlin.uuid.toJavaUuid
import kotlin.uuid.toKotlinUuid

fun UserArticleState.toPO(): UserArticleStatePO {
    return UserArticleStatePO(
        id = UserArticleStateId(
            userId = this.userId.value.toJavaUuid(),
            articleId = this.articleId.value.toJavaUuid()
        ),
        read = this.read,
        starred = this.starred,
        gmtCreate = null,
        gmtModify = null
    )
}

fun UserArticleStatePO.toDomain(): UserArticleState {
    val pk = requireNotNull(this.id) { "UserArticleStatePO.id must not be null" }
    return UserArticleState(
        userId = UserId(pk.userId.toKotlinUuid()),
        articleId = ArticleId(pk.articleId.toKotlinUuid()),
        read = this.read,
        starred = this.starred ?: false   // 若 starred 为 null，则默认 false（但数据库非空，实际不会为 null）
    )
}