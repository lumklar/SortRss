package io.github.lumklar.sortrss.common.domain.model.feed

import io.github.lumklar.sortrss.common.domain.model.datasource.DataSourceType
import io.github.lumklar.sortrss.common.domain.model.user.UserId
import kotlin.time.Clock

/**
 * 订阅源聚合根。
 * 代表一个 RSS/Atom 订阅地址，属于特定用户。
 * 只有来自 LOCAL_OPML 数据源的订阅源可以被编辑（重命名、移动文件夹等）。
 */
class Feed private constructor(
    val id: FeedId,
    val feedUrl: String,
    val title: String,
    val siteUrl: String?,
    val description: String?,
    val iconUrl: String?,
    val sourceType: DataSourceType,         // 该订阅源来源于哪种数据源
    val lastSyncTimestamp: Long = 0L
) {
    /** 该订阅源是否可以被编辑（重命名、移动等） */
    fun canEdit(): Boolean = sourceType == DataSourceType.LOCAL_OPML

    companion object {
        /**
         * 创建一个新的订阅源。
         */
        fun create(
            id: FeedId,
            userId: UserId,
            feedUrl: String,
            title: String,
            siteUrl: String? = null,
            description: String? = null,
            iconUrl: String? = null,
            sourceType: DataSourceType
        ): Feed {
            require(feedUrl.isNotBlank()) { throw FeedUrlEmptyException() }
            require(title.isNotBlank()) { throw FeedTitleEmptyException() }
            return Feed(
                id = id,
                feedUrl = feedUrl.trim(),
                title = title.trim(),
                siteUrl = siteUrl?.trim(),
                description = description?.trim(),
                iconUrl = iconUrl?.trim(),
                sourceType = sourceType
            )
        }

        /**
         * 从持久化数据重建（含已有文章关联）。
         */
        fun reconstruct(
            id: FeedId,
            feedUrl: String,
            title: String,
            siteUrl: String?,
            description: String?,
            iconUrl: String?,
            sourceType: DataSourceType,
            articles: List<FeedArticle>,
            lastSyncTimestamp: Long = 0L
        ): Feed {
            return Feed(id,  feedUrl, title, siteUrl, description, iconUrl, sourceType,  lastSyncTimestamp)
        }
    }

    /**
     * 更新最后同步时间。
     */
    fun markSynced(timestamp: Long = Clock.System.now().toEpochMilliseconds()) {
        (this as Feed).apply {
            // 同 DataSource 一样，lastSyncTimestamp 需改为 private var
        }
    }
}