package io.github.lumklar.sortrss.server.infrastructure.persistence.article.repository

import io.github.lumklar.sortrss.common.domain.model.article.ArticleId
import io.github.lumklar.sortrss.common.domain.model.article.UserArticleState
import io.github.lumklar.sortrss.common.domain.model.article.UserArticleStateRepository
import io.github.lumklar.sortrss.common.domain.model.user.UserId
import io.github.lumklar.sortrss.server.infrastructure.persistence.article.convert.toDomain
import io.github.lumklar.sortrss.server.infrastructure.persistence.article.convert.toPO
import io.github.lumklar.sortrss.server.infrastructure.persistence.article.entity.UserArticleStateId
import io.github.lumklar.sortrss.server.infrastructure.persistence.article.repository.jpa.UserArticleStateJpaRepository
import org.springframework.stereotype.Component
import kotlin.uuid.toJavaUuid

@Component
class UserArticleStateRepositoryImpl(
    private val jpaRepo: UserArticleStateJpaRepository
) : UserArticleStateRepository {

    override fun save(state: UserArticleState): UserArticleState {
        val po = state.toPO()
        val saved = jpaRepo.save(po)
        return saved.toDomain()
    }

    override fun findByUserAndArticle(userId: UserId, articleId: ArticleId): UserArticleState? {
        val id = UserArticleStateId(userId.value.toJavaUuid(), articleId.value.toJavaUuid())
        return jpaRepo.findById(id)
            .map { it.toDomain() }
            .orElse(null)
    }

    override fun findAllByUser(userId: UserId): List<UserArticleState> =
        jpaRepo.findByIdUserId(userId.value.toJavaUuid())
            .map { it.toDomain() }

    override fun deleteByUserAndArticle(userId: UserId, articleId: ArticleId) {
        jpaRepo.deleteByIdUserIdAndIdArticleId(userId.value.toJavaUuid(), articleId.value.toJavaUuid())
    }
}
