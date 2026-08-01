package io.github.lumklar.sortrss.server.infrastructure.persistence.folder.repository.jpa

import io.github.lumklar.sortrss.server.infrastructure.persistence.folder.entity.FolderPO
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface JpaFolderRepository : JpaRepository<FolderPO, UUID> {
    fun findByDataSourceId(dataSourceId: UUID): List<FolderPO>
}
