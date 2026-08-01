package io.github.lumklar.sortrss.server.infrastructure.persistence.article.repository.jpa

import io.github.lumklar.sortrss.server.infrastructure.persistence.article.entity.UserArticleStateId
import io.github.lumklar.sortrss.server.infrastructure.persistence.article.entity.UserArticleStatePO
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserArticleStateJpaRepository : JpaRepository<UserArticleStatePO, UserArticleStateId> {
    fun findByIdUserId(userId: UUID): List<UserArticleStatePO>
    fun deleteByIdUserIdAndIdArticleId(userId: UUID, articleId: UUID)
}
