package io.github.lumklar.sortrss.server.infrastructure.persistence.folder.entity

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.io.Serializable
import java.util.*

@Embeddable
data class FolderClosureId(
    @Column(name = "ancestor", columnDefinition = "UUID")
    var ancestor: UUID = UUID(0, 0),

    @Column(name = "descendant", columnDefinition = "UUID")
    var descendant: UUID = UUID(0, 0)
) : Serializable