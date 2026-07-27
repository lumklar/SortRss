package io.github.lumklar.sortrss.server.infrastructure.persistence.datasource.repository.jpa

import io.github.lumklar.sortrss.server.infrastructure.persistence.datasource.entity.DataSourceFeedSubscriptionPo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface DataSourceFeedSubscriptionJpaRepository :
    JpaRepository<DataSourceFeedSubscriptionPo, UUID> {

    fun findByDataSourceId(dataSourceId: UUID): List<DataSourceFeedSubscriptionPo>
    fun findByFeedId(feedId: UUID): List<DataSourceFeedSubscriptionPo>
}