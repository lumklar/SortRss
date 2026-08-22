package io.github.lumklar.sortrss.common.domain.model.feed

import io.github.lumklar.sortrss.common.domain.shared.enums.DataSourceType
import kotlin.time.Clock
import kotlin.time.Instant

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
    initialLastSyncTime: Instant? = null    // 初始同步时间（可为空）
) {
    /** 最后一次同步时间，外部只读，内部可修改 */
    var lastSyncTime: Instant? = initialLastSyncTime
        private set

    /** 该订阅源是否可以被编辑（重命名、移动等） */
    fun canEdit(): Boolean = sourceType == DataSourceType.LOCAL_OPML

    /**
     * 更新最后同步时间为当前时刻。
     */
    fun markSynced() {
        markSyncCompleted(Clock.System.now())
    }

    /**
     * 同步完成后调用，更新最后同步时间。
     */
    fun markSyncCompleted(syncTime: Instant) {
        require(syncTime >= (lastSyncTime ?: syncTime)) {
            "Sync time cannot be earlier than last sync time: $lastSyncTime"
        }
        this.lastSyncTime = syncTime
    }

    companion object {
        /**
         * 创建一个新地订阅源（初始同步时间为 null）。
         */
        fun create(
            id: FeedId,
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
                sourceType = sourceType,
                initialLastSyncTime = null   // 新建时无同步记录
            )
        }

        /**
         * 从持久化数据重建（含已有文章关联及初始同步时间）。
         */
        fun reconstruct(
            id: FeedId,
            feedUrl: String,
            title: String,
            siteUrl: String?,
            description: String?,
            iconUrl: String?,
            sourceType: DataSourceType,
            initialLastSyncTime: Instant? = null
        ): Feed {
            return Feed(
                id = id,
                feedUrl = feedUrl,
                title = title,
                siteUrl = siteUrl,
                description = description,
                iconUrl = iconUrl,
                sourceType = sourceType,
                initialLastSyncTime = initialLastSyncTime
            )
        }
    }
}