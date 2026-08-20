package io.github.lumklar.sortrss.server.infrastructure.persistence.datasource.entity;

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.util.*

@Entity
@Table(
    name = "data_source_feed_subscription",
    uniqueConstraints = [UniqueConstraint(columnNames = ["data_source_id", "feed_id"])],
    indexes = [
        Index(name = "idx_ds_feed_sub_data_source_id", columnList = "data_source_id"),
        Index(name = "idx_ds_feed_sub_feed_id", columnList = "feed_id")
    ]
)
class DataSourceFeedSubscriptionPo(
    @Id
    @Column(name = "id", nullable = false)
    var id: UUID? = null,

    @Column(name = "data_source_id", nullable = false)
    var dataSourceId: UUID? = null,

    @Column(name = "feed_id", nullable = false)
    var feedId: UUID? = null,

    @Column(name = "custom_title", length = 255)
    var customTitle: String? = null,

    @Column(name = "source_feed_id", length = 255)
    var sourceFeedId: String? = null,

    @Column(name = "last_all_read_at")
    var lastAllReadAt: Instant? = null,

    @CreationTimestamp
    @Column(name = "gmt_create", nullable = false, updatable = false)
    var gmtCreate: Instant? = null,

    @UpdateTimestamp
    @Column(name = "gmt_modify", nullable = false)
    var gmtModify: Instant? = null
)