package io.github.lumklar.sortrss.common.domain.model.article

/**
 * 文章聚合根。
 * 代表一篇完整的文章内容，独立于任何订阅源存在（通过关联类 FeedArticle 建立与订阅源的关系）。
 */
class Article private constructor(
    val id: ArticleId,
    val title: String,
    val author: String?,
    val summary: String?,
    val content: String?,
    val link: String,
    val publishedAt: Long,      // 发布时间戳（毫秒）
    val updatedAt: Long?       // 最后更新时间戳（毫秒）
) {
    companion object {
        /**
         * 创建一篇新文章。
         * @throws ArticleTitleEmptyException 如果标题为空
         * @throws ArticleLinkEmptyException 如果链接为空
         */
        fun create(
            id: ArticleId,
            title: String,
            author: String? = null,
            summary: String? = null,
            content: String? = null,
            link: String,
            publishedAt: Long,
            updatedAt: Long? = null
        ): Article {
            require(title.isNotBlank()) { throw ArticleTitleEmptyException() }
            require(link.isNotBlank()) { throw ArticleLinkEmptyException() }
            return Article(
                id = id,
                title = title.trim(),
                author = author?.trim(),
                summary = summary?.trim(),
                content = content?.trim(),
                link = link.trim(),
                publishedAt = publishedAt,
                updatedAt = updatedAt
            )
        }

        /**
         * 从持久化重建（无额外校验，假设数据已合法）。
         */
        fun reconstruct(
            id: ArticleId,
            title: String,
            author: String?,
            summary: String?,
            content: String?,
            link: String,
            publishedAt: Long,
            updatedAt: Long?
        ): Article {
            return Article(id, title, author, summary, content, link, publishedAt, updatedAt)
        }
    }

    /**
     * 更新文章元数据（标题、内容等）。通常由同步服务调用。
     */
    fun update(
        newTitle: String? = null,
        newAuthor: String? = null,
        newSummary: String? = null,
        newContent: String? = null,
        newLink: String? = null,
        newUpdatedAt: Long? = null
    ): Article {
        return copy(
            title = newTitle?.trim() ?: title,
            author = newAuthor?.trim() ?: author,
            summary = newSummary?.trim() ?: summary,
            content = newContent?.trim() ?: content,
            link = newLink?.trim() ?: link,
            updatedAt = newUpdatedAt ?: updatedAt
        )
    }

    // 利用 data class 特性生成 copy，将 Article 改为 data class 会便利一些，
    // 但当前为普通类，我们可提供一个私有 copy 方法或直接暴露一个内部修改方法。
    // 为了简洁，这里将 Article 声明改为 data class 是合理的（值对象偏向）。
    // 若坚持充血模型不暴露 data class，可保留下面的实现：
    private fun copy(
        title: String,
        author: String?,
        summary: String?,
        content: String?,
        link: String,
        updatedAt: Long?
    ): Article = Article(id, title, author, summary, content, link, publishedAt, updatedAt)
}