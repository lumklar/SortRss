package io.github.lumklar.sortrss.common.domain.model.article

interface ArticleRepository {
    fun save(article: Article): Article
    fun findById(id: ArticleId): Article?
    fun findByGuid(guid: String): Article?
    fun findAllByIds(ids: List<ArticleId>): List<Article>
    fun delete(id: ArticleId)
}
