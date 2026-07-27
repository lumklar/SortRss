package io.github.lumklar.sortrss.server.infrastructure.persistence.datasource.repository

import io.github.lumklar.sortrss.common.domain.model.datasource.DataSourceFeedSubscription
import io.github.lumklar.sortrss.common.domain.model.datasource.DataSourceFeedSubscriptionRepository
import io.github.lumklar.sortrss.common.domain.model.datasource.DataSourceId
import io.github.lumklar.sortrss.common.domain.model.feed.FeedId
import io.github.lumklar.sortrss.server.infrastructure.persistence.datasource.convert.toDomain
import io.github.lumklar.sortrss.server.infrastructure.persistence.datasource.convert.toPo
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

}