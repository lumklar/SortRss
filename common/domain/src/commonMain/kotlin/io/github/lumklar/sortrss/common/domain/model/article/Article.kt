package io.github.lumklar.sortrss.common.domain.model.article

import kotlin.time.Instant

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
    val publishedAt: Instant,      // 发布时间戳（毫秒）
    val updatedAt: Instant?,       // 最后更新时间戳（毫秒）
    val guid: String? = null    // 新增：源端全局唯一标识符
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
            publishedAt: Instant,
            updatedAt: Instant? = null,
            guid: String? = null       // 新增参数
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
                updatedAt = updatedAt,
                guid = guid?.trim()
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
            publishedAt: Instant,
            updatedAt: Instant?,
            guid: String? = null       // 新增参数
        ): Article {
            return Article(id, title, author, summary, content, link, publishedAt, updatedAt, guid)
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
        newUpdatedAt: Instant? = null,
        newGuid: String? = null        // 新增参数
    ): Article {
        return copy(
            title = newTitle?.trim() ?: title,
            author = newAuthor?.trim() ?: author,
            summary = newSummary?.trim() ?: summary,
            content = newContent?.trim() ?: content,
            link = newLink?.trim() ?: link,
            updatedAt = newUpdatedAt ?: updatedAt,
            guid = newGuid?.trim() ?: guid
        )
    }

    // 私有 copy 方法，用于 update 内部构造新实例
    private fun copy(
        title: String,
        author: String?,
        summary: String?,
        content: String?,
        link: String,
        updatedAt: Instant?,
        guid: String?
    ): Article = Article(id, title, author, summary, content, link, publishedAt, updatedAt, guid)
}