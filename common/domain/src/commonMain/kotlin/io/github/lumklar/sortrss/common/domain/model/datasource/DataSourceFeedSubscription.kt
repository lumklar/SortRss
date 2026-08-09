package io.github.lumklar.sortrss.common.domain.model.datasource

import io.github.lumklar.sortrss.common.domain.model.feed.FeedId
import kotlin.time.Instant

class DataSourceFeedSubscription private constructor(
    val dataSourceId: DataSourceId,
    val feedId: FeedId,
    val customTitle: String?,
    val sourceFeedId: String? = null,
    val lastAllReadAt: Instant? = null   // 新增：该订阅下用户全部已读时间戳
) {
    // 手动 copy，支持新字段
    fun copy(
        dataSourceId: DataSourceId = this.dataSourceId,
        feedId: FeedId = this.feedId,
        customTitle: String? = this.customTitle,
        sourceFeedId: String? = this.sourceFeedId,
        lastAllReadAt: Instant? = this.lastAllReadAt
    ): DataSourceFeedSubscription =
        createInternal(dataSourceId, feedId, customTitle, sourceFeedId, lastAllReadAt)

    // 业务方法：更改自定义标题
    fun changeCustomTitle(newTitle: String?): DataSourceFeedSubscription =
        copy(customTitle = newTitle?.trim())

    // 业务方法：标记该订阅下所有文章为已读
    fun markAllRead(readTime: Instant): DataSourceFeedSubscription =
        copy(lastAllReadAt = readTime)

    // 业务相等性（基于业务主键）
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DataSourceFeedSubscription) return false
        return dataSourceId == other.dataSourceId && feedId == other.feedId
    }

    override fun hashCode(): Int = 31 * dataSourceId.hashCode() + feedId.hashCode()

    companion object {
        // 私有工厂：集中处理所有校验和规范化
        private fun createInternal(
            dataSourceId: DataSourceId,
            feedId: FeedId,
            customTitle: String?,
            sourceFeedId: String?,
            lastAllReadAt: Instant? = null   // 新增参数
        ): DataSourceFeedSubscription {
            return DataSourceFeedSubscription(
                dataSourceId = dataSourceId,
                feedId = feedId,
                customTitle = customTitle?.trim(),
                sourceFeedId = sourceFeedId,
                lastAllReadAt = lastAllReadAt
            )
        }

        // 公开的工厂方法
        fun create(
            dataSourceId: DataSourceId,
            feedId: FeedId,
            remoteId: String? = null,
            customTitle: String? = null,
            lastAllReadAt: Instant? = null   // 新增参数
        ): DataSourceFeedSubscription =
            createInternal(dataSourceId, feedId, customTitle, remoteId, lastAllReadAt)
    }
}