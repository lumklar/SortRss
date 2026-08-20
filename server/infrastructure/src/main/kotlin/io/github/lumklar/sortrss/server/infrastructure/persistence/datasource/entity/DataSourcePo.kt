package io.github.lumklar.sortrss.server.infrastructure.persistence.datasource.entity;

import io.github.lumklar.sortrss.common.domain.shared.enums.DataSourceType
import io.github.lumklar.sortrss.server.infrastructure.persistence.common.convert.DataSourceTypeConverter
import io.github.lumklar.sortrss.server.infrastructure.persistence.datasource.convert.DataSourceConnectionDetailsConverter
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.util.*

@Entity
@Table(
    name = "data_source",
    indexes = [
        Index(name = "idx_data_source_user_id", columnList = "user_id"),
        Index(name = "idx_data_source_type", columnList = "type"),
        Index(name = "uk_data_source_unique_key", columnList = "unique_key", unique = true)
    ]
)
class DataSourcePo(
    @Id
    @Column(name = "id", nullable = false)
    var id: UUID? = null,

    @Column(name = "user_id", nullable = false)
    var userId: UUID? = null,

    @Convert(converter = DataSourceTypeConverter::class)
    @Column(name = "type", nullable = false)
    var type: DataSourceType? = null,

    @Column(name = "name", nullable = false, length = 255)
    var name: String? = null,

    @Convert(converter = DataSourceConnectionDetailsConverter::class)
    @Column(name = "connection_details", columnDefinition = "TEXT")
    var connectionDetails: String? = null,

    @Column(name = "unique_key", nullable = false, length = 512)
    var uniqueKey: String? = null,

    @Column(name = "last_sync_time")
    var lastSyncTime: Instant? = null,

    @Column(name = "last_all_read_at")
    var lastAllReadAt: Instant? = null,

    @CreationTimestamp
    @Column(name = "gmt_create", nullable = false, updatable = false)
    var gmtCreate: Instant? = null,

    @UpdateTimestamp
    @Column(name = "gmt_modify", nullable = false)
    var gmtModify: Instant? = null
)