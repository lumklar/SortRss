package io.github.lumklar.sortrss.server.infrastructure.persistence.article.repository


import io.github.lumklar.sortrss.common.domain.model.article.Article
import io.github.lumklar.sortrss.common.domain.model.article.ArticleId
import io.github.lumklar.sortrss.common.domain.model.article.ArticleRepository
import io.github.lumklar.sortrss.server.infrastructure.persistence.article.convert.toDomain
import io.github.lumklar.sortrss.server.infrastructure.persistence.article.convert.toPO
import io.github.lumklar.sortrss.server.infrastructure.persistence.article.repository.jpa.ArticleJpaRepository
import org.springframework.stereotype.Component
import kotlin.uuid.toJavaUuid

@Component
class ArticleRepositoryImpl(
    private val jpaRepo: ArticleJpaRepository
) : ArticleRepository {

    override fun save(article: Article): Article {
        val po = article.toPO()
        val saved = jpaRepo.save(po)
        return saved.toDomain()
    }

    override fun findById(id: ArticleId): Article? =
        jpaRepo.findById(id.value.toJavaUuid())
            .map { it.toDomain() }
            .orElse(null)

    override fun findByGuid(guid: String): Article? =
        jpaRepo.findByGuid(guid)?.let { it.toDomain() }

    override fun findAllByIds(ids: List<ArticleId>): List<Article> =
        jpaRepo.findAllByIdIn(ids.map { it.value.toJavaUuid() })
            .map { it.toDomain() }

    override fun delete(id: ArticleId) {
        jpaRepo.deleteById(id.value.toJavaUuid())
    }
}