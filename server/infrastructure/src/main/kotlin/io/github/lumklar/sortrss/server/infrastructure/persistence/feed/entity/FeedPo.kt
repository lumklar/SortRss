package io.github.lumklar.sortrss.server.infrastructure.persistence.feed.entity;

import io.github.lumklar.sortrss.common.domain.shared.enums.DataSourceType;
import io.github.lumklar.sortrss.server.infrastructure.persistence.common.convert.DataSourceTypeConverter;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
    name = "feeds",
    uniqueConstraints = [UniqueConstraint(columnNames = ["feed_url"])],
    indexes = [
        Index(name = "idx_feeds_source_type", columnList = "source_type"),
        Index(name = "idx_feeds_last_sync_time", columnList = "last_sync_time")
    ]
)
class FeedPo(
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    var id: UUID? = null,

    @Column(name = "feed_url", nullable = false, length = 2048)
    var feedUrl: String? = null,

    @Column(name = "title", nullable = false, length = 255)
    var title: String? = null,

    @Column(name = "site_url", length = 2048)
    var siteUrl: String? = null,

    @Column(name = "description", columnDefinition = "TEXT")
    var description: String? = null,

    @Column(name = "icon_url", length = 2048)
    var iconUrl: String? = null,

    @Convert(converter = DataSourceTypeConverter::class)
    @Column(name = "source_type", nullable = false)
    var sourceType: DataSourceType? = null,

    @Column(name = "last_sync_time")
    var lastSyncTime: Instant? = null,

    @CreationTimestamp
    @Column(name = "gmt_create", nullable = false, updatable = false)
    var gmtCreate: Instant? = null,

    @UpdateTimestamp
    @Column(name = "gmt_modify", nullable = false)
    var gmtModify: Instant? = null
)