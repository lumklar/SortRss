// ==================== Entry 实体（全局） ====================
package io.github.lumklar.sortrss.common.domain.model.entity

import io.github.lumklar.sortrss.common.domain.shared.valueobject.EntryId
import io.github.lumklar.sortrss.common.domain.shared.valueobject.FeedId
import kotlin.time.Instant

/**
 * 一篇文章，全局唯一。
 * 使用 feedId + uniqueKey 作为业务唯一标识，uniqueKey 由应用层生成（如 guid / link+published 等）。
 */
class Entry private constructor(
    val id: EntryId,
    val feedId: FeedId,
    val uniqueKey: String,          // 业务唯一键（如 GUID、link+published 组合）
    var title: String,
    var link: String?,
    var author: String?,
    var content: String?,
    var summary: String?,
    var publishedAt: Instant?,      // 发布时间
    var updatedAt: Instant?         // 内容最后修改时间，用于合并
) {
    companion object {
        fun create(
            feedId: FeedId,
            uniqueKey: String,
            title: String,
            link: String? = null,
            author: String? = null,
            content: String? = null,
            summary: String? = null,
            publishedAt: Instant? = null,
            updatedAt: Instant? = null,
            id: EntryId = EntryId(0)
        ): Entry {
            require(uniqueKey.isNotBlank()) { "业务唯一键不能为空" }
            require(title.isNotBlank()) { "文章标题不能为空" }
            return Entry(id, feedId, uniqueKey.trim(), title.trim(), link?.trim(), author?.trim(), content, summary?.trim(), publishedAt, updatedAt)
        }
    }

    /**
     * 用新同步的数据合并，当新数据的更新时间更晚时覆盖内容。
     * 返回是否发生了实际变更。
     */
    fun mergeContent(
        newTitle: String,
        newLink: String?,
        newAuthor: String?,
        newContent: String?,
        newSummary: String?,
        newPublishedAt: Instant?,
        newUpdatedAt: Instant?
    ): Boolean {
        if (newUpdatedAt == null || (updatedAt != null && updatedAt >= newUpdatedAt)) {
            return false
        }
        title = newTitle.trim()
        link = newLink?.trim()
        author = newAuthor?.trim()
        content = newContent
        summary = newSummary?.trim()
        publishedAt = newPublishedAt
        updatedAt = newUpdatedAt
        return true
    }
}