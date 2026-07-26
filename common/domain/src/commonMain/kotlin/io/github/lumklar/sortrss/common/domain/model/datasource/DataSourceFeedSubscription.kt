package io.github.lumklar.sortrss.common.domain.model.datasource

import io.github.lumklar.sortrss.common.domain.model.feed.FeedId

class DataSourceFeedSubscription private constructor(
    val dataSourceId: DataSourceId,
    val feedId: FeedId,
    val customTitle: String?,
    val remoteId: String?
) {
    // 手动 copy，直接委托给私有工厂
    fun copy(
        dataSourceId: DataSourceId = this.dataSourceId,
        feedId: FeedId = this.feedId,
        customTitle: String? = this.customTitle,
        remoteId: String? = this.remoteId
    ): DataSourceFeedSubscription =
        createInternal(dataSourceId, feedId, customTitle,  remoteId)

    // 业务方法：更改自定义标题
    //TODO 领域模型封装内部方法，需要判断数据来源
    fun changeCustomTitle(newTitle: String?): DataSourceFeedSubscription =
        copy(customTitle = newTitle?.trim())

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
            remoteId: String?
        ): DataSourceFeedSubscription {
            require(dataSourceId.value > 0) { "dataSourceId must be positive" }
            require(feedId.value > 0) { "feedId must be positive" }
            return DataSourceFeedSubscription(
                dataSourceId = dataSourceId,
                feedId = feedId,
                customTitle = customTitle?.trim(),
                remoteId = remoteId
            )
        }

        // 公开的工厂方法
        fun create(
            dataSourceId: DataSourceId,
            feedId: FeedId,
            remoteId: String? = null,
            customTitle: String? = null
        ): DataSourceFeedSubscription =
            createInternal(dataSourceId, feedId, customTitle, remoteId)
    }
}