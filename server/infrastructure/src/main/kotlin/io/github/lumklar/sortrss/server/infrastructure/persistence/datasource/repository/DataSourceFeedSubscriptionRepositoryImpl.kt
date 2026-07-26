package io.github.lumklar.sortrss.server.infrastructure.persistence.datasource.repository

import io.github.lumklar.sortrss.common.domain.model.datasource.DataSourceFeedSubscription
import io.github.lumklar.sortrss.common.domain.model.datasource.DataSourceFeedSubscriptionRepository
import io.github.lumklar.sortrss.common.domain.model.datasource.DataSourceId
import io.github.lumklar.sortrss.common.domain.model.feed.FeedId
import io.github.lumklar.sortrss.server.infrastructure.persistence.datasource.convert.toDomain
import io.github.lumklar.sortrss.server.infrastructure.persistence.datasource.convert.toPo
import io.github.lumklar.sortrss.server.infrastructure.persistence.datasource.entity.DataSourceFeedSubscriptionId
import io.github.lumklar.sortrss.server.infrastructure.persistence.datasource.repository.jpa.DataSourceFeedSubscriptionJpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import kotlin.uuid.toJavaUuid

@Repository
@Transactional
class DataSourceFeedSubscriptionRepositoryImpl(
    private val jpaRepository: DataSourceFeedSubscriptionJpaRepository
) : DataSourceFeedSubscriptionRepository {

    override fun save(subscription: DataSourceFeedSubscription): DataSourceFeedSubscription {
        val po = subscription.toPo()
        val saved = jpaRepository.save(po)
        return saved.toDomain()
    }

    override fun findByDataSourceId(dataSourceId: DataSourceId): List<DataSourceFeedSubscription> =
        jpaRepository.findByDataSourceId(dataSourceId.value.toJavaUuid()).map { it.toDomain() }

    override fun findByFeedId(feedId: FeedId): List<DataSourceFeedSubscription> =
        jpaRepository.findByFeedId(feedId.value.toJavaUuid()).map { it.toDomain() }

    override fun findById(dataSourceId: DataSourceId, feedId: FeedId): DataSourceFeedSubscription? =
        jpaRepository.findById(DataSourceFeedSubscriptionId(dataSourceId.value.toJavaUuid(), feedId.value.toJavaUuid()))
            .orElse(null)?.toDomain()

    override fun delete(subscription: DataSourceFeedSubscription) {
        jpaRepository.deleteById(
            DataSourceFeedSubscriptionId(
                subscription.dataSourceId.value.toJavaUuid(),
                subscription.feedId.value.toJavaUuid()
            )
        )
    }
}