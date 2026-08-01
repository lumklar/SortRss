package io.github.lumklar.sortrss.common.domain.model.article

import io.github.lumklar.sortrss.common.domain.model.user.UserId

interface UserArticleStateRepository {
    fun save(state: UserArticleState): UserArticleState
    fun findByUserAndArticle(userId: UserId, articleId: ArticleId): UserArticleState?
    fun findAllByUser(userId: UserId): List<UserArticleState>
    fun deleteByUserAndArticle(userId: UserId, articleId: ArticleId)
}