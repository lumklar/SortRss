// ==================== Feed 实体（全局） ====================
package io.github.lumklar.sortrss.common.domain.model.entity

import io.github.lumklar.sortrss.common.domain.shared.valueobject.FeedId
import kotlin.time.Instant

/**
 * 全局唯一的 RSS/Atom 订阅源。
 * 由 URL 唯一标识，被用户通过不同数据源订阅时共享。
 */
class Feed private constructor(
    val id: FeedId,
    val url: String,
    var title: String,
    var description: String,
    var siteUrl: String?,
    var lastBuildDate: Instant?,                  // 订阅源最后构建时间
    var updatedAt: Instant?                       // 最后从源更新元数据的时间
) {
    companion object {
        fun create(
            url: String,
            title: String,
            description: String = "",
            siteUrl: String? = null,
            lastBuildDate: Instant? = null,
            updatedAt: Instant? = null,
            id: FeedId = FeedId(0)
        ): Feed {
            require(url.isNotBlank()) { "Feed URL 不能为空" }
            return Feed(id, url.trim(), title.trim(), description.trim(), siteUrl?.trim(), lastBuildDate, updatedAt)
        }
    }

    /**
     * 用新抓取的元数据更新，但仅在时间戳更新时覆盖。
     * 返回是否发生了变更。
     */
    fun mergeMeta(
        newTitle: String,
        newDescription: String,
        newSiteUrl: String?,
        newLastBuildDate: Instant?,
        newUpdatedAt: Instant?
    ): Boolean {
        // 只有在新更新时间严格晚于已记录时间时才更新
        if (newUpdatedAt == null || (updatedAt != null && updatedAt >= newUpdatedAt)) {
            return false
        }
        title = newTitle.trim()
        description = newDescription.trim()
        siteUrl = newSiteUrl?.trim()
        lastBuildDate = newLastBuildDate
        updatedAt = newUpdatedAt
        return true
    }
}