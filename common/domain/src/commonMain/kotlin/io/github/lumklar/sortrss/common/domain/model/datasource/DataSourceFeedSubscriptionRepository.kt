package io.github.lumklar.sortrss.common.domain.model.datasource

import io.github.lumklar.sortrss.common.domain.model.feed.FeedId

interface DataSourceFeedSubscriptionRepository {
    fun save(subscription: DataSourceFeedSubscription): DataSourceFeedSubscription
    fun findByDataSourceId(dataSourceId: DataSourceId): List<DataSourceFeedSubscription>
    fun findByFeedId(feedId: FeedId): List<DataSourceFeedSubscription>
    fun findById(dataSourceId: DataSourceId, feedId: FeedId): DataSourceFeedSubscription?
    fun delete(subscription: DataSourceFeedSubscription)
}