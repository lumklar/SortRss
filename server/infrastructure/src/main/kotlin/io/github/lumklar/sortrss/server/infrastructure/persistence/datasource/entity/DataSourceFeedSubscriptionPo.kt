package io.github.lumklar.sortrss.server.infrastructure.persistence.datasource.entity

import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Entity
@Table(name = "data_source_feed_subscription")
class DataSourceFeedSubscriptionPo {

    @Id
    var id: UUID? = null   // 通常订阅表可自增主键

    @Column(name = "data_source_id", nullable = false, columnDefinition = "BINARY(16)")
    var dataSourceId: UUID? = null

    @Column(name = "feed_id", nullable = false, columnDefinition = "BINARY(16)")
    var feedId: UUID? = null

    @Column(name = "custom_title")
    var customTitle: String? = null

    @Column(name = "remote_id")
    var remoteId: String? = null

    @Column(name = "last_all_read_at")
    var lastAllReadAt: Instant? = null
}
