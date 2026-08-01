package io.github.lumklar.sortrss.server.infrastructure.persistence.folder.repository.jpa

import io.github.lumklar.sortrss.server.infrastructure.persistence.folder.entity.FolderClosureId
import io.github.lumklar.sortrss.server.infrastructure.persistence.folder.entity.FolderClosurePO
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional
import java.util.*

interface JpaFolderClosureRepository : JpaRepository<FolderClosurePO, FolderClosureId> {

    @Query("SELECT f.id.descendant FROM FolderClosurePO f WHERE f.id.ancestor = :ancestor")
    fun findDescendantsByAncestor(ancestor: UUID): List<UUID>

    @Modifying
    @Transactional
    @Query("DELETE FROM FolderClosurePO f WHERE f.id.descendant IN :descendants")
    fun deleteByDescendantIn(descendants: List<UUID>)

    @Modifying
    @Transactional
    @Query("DELETE FROM FolderClosurePO f WHERE f.id.ancestor IN :nodes OR f.id.descendant IN :nodes")
    fun deleteByAncestorInOrDescendantIn(nodes: List<UUID>)
}
