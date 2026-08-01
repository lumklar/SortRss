package io.github.lumklar.sortrss.server.infrastructure.persistence.folder.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.Instant;

@Entity
@Table(name = "folder_closure")
class FolderClosurePO(
    @EmbeddedId
    var id: FolderClosureId? = null,

    @Column(name = "depth", nullable = false)
    var depth: Int? = null,

    @CreationTimestamp
    @Column(name = "gmt_create", nullable = false, updatable = false)
    var gmtCreate: Instant? = null,

    @UpdateTimestamp
    @Column(name = "gmt_modify", nullable = false)
    var gmtModify: Instant? = null
)