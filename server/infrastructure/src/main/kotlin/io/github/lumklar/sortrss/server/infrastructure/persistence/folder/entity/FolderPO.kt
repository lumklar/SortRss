package io.github.lumklar.sortrss.server.infrastructure.persistence.folder.entity

import io.github.lumklar.sortrss.common.domain.shared.enums.DataSourceType
import io.github.lumklar.sortrss.server.infrastructure.persistence.common.convert.DataSourceTypeConverter
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.util.*

@Entity
@Table(
    name = "folder",
    indexes = [
        Index(name = "idx_folder_data_source_id", columnList = "dataSourceId"),
        Index(name = "idx_folder_parent_folder_id", columnList = "parentFolderId")
    ]
)
class FolderPO(
    @Id
    @Column(nullable = false)
    var id: UUID? = null,

    @Column(name = "data_source_id", nullable = false)
    var dataSourceId: UUID? = null,

    @Column(name = "name", nullable = false, length = 255)
    var name: String? = null,

    @Column(name = "parent_folder_id")
    var parentFolderId: UUID? = null,

    @Convert(converter = DataSourceTypeConverter::class)
    @Column(name = "data_source_type", nullable = false)
    var dataSourceType: DataSourceType? = null,

    // 新增：数据源中的分组ID
    @Column(name = "source_group_id", length = 255)
    var sourceGroupId: String? = null,

    @CreationTimestamp
    @Column(name = "gmt_create", nullable = false, updatable = false)
    var gmtCreate: Instant? = null,

    @UpdateTimestamp
    @Column(name = "gmt_modify", nullable = false)
    var gmtModify: Instant? = null
)