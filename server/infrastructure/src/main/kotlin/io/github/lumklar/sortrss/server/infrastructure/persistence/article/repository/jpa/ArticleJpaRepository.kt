package io.github.lumklar.sortrss.server.infrastructure.persistence.article.repository.jpa

import io.github.lumklar.sortrss.server.infrastructure.persistence.article.entity.ArticlePO
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ArticleJpaRepository : JpaRepository<ArticlePO, UUID> {
    fun findByGuid(guid: String): ArticlePO?
    fun findAllByIdIn(ids: List<UUID>): List<ArticlePO>
}
