package io.github.lumklar.sortrss.server.infrastructure.persistence.folder.entity;

import io.github.lumklar.sortrss.common.domain.shared.enums.DataSourceType;
import io.github.lumklar.sortrss.server.infrastructure.persistence.common.convert.DataSourceTypeConverter;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
    name = "folder_membership",
    uniqueConstraints = [UniqueConstraint(columnNames = ["feed_id"])],
    indexes = [Index(name = "idx_folder_membership_folder_id", columnList = "folderId")]
)
class FolderMembershipPO(
    @Id
    @Column(nullable = false)
    var id: UUID? = null,

    @Column(name = "folder_id", nullable = false)
    var folderId: UUID? = null,

    @Column(name = "feed_id", nullable = false)
    var feedId: UUID? = null,

    @Convert(converter = DataSourceTypeConverter::class)
    @Column(name = "data_source_type", nullable = false)
    var dataSourceType: DataSourceType? = null,

    @CreationTimestamp
    @Column(name = "gmt_create", nullable = false, updatable = false)
    var gmtCreate: Instant? = null,

    @UpdateTimestamp
    @Column(name = "gmt_modify", nullable = false)
    var gmtModify: Instant? = null
)