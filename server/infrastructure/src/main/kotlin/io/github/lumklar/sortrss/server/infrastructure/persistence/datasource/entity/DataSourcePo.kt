package io.github.lumklar.sortrss.server.infrastructure.persistence.datasource.entity

import io.github.lumklar.sortrss.server.infrastructure.persistence.datasource.convert.DataSourceConnectionDetailsConverter
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "data_source")
class DataSourcePo {

    @Id
    @Column(columnDefinition = "BINARY(16)")   // 明确指定二进制存储
    var id: UUID? = null

    @Column(name = "user_id", nullable = false, columnDefinition = "BINARY(16)")
    var userId: UUID? = null

    @Column(name = "type", nullable = false)
    var type: Int? = null  // 由 DataSourceTypeConverter 自动转换

    @Column(name = "name", nullable = false, length = 50)
    var name: String? = null

    @Convert(converter = DataSourceConnectionDetailsConverter::class)
    @Column(name = "connection_details", columnDefinition = "TEXT")
    var connectionDetails: String? = null

    @Column(name = "last_sync_time")
    var lastSyncTime: Instant? = null

    @Column(name = "last_all_read_at")
    var lastAllReadAt: Instant? = null
}