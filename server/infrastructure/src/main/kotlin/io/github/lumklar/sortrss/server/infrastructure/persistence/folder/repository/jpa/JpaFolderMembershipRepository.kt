package io.github.lumklar.sortrss.server.infrastructure.persistence.folder.repository.jpa

import io.github.lumklar.sortrss.server.infrastructure.persistence.folder.entity.FolderMembershipPO
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface JpaFolderMembershipRepository : JpaRepository<FolderMembershipPO, UUID> {
    fun findByFeedId(feedId: UUID): Optional<FolderMembershipPO>
    fun findByFolderId(folderId: UUID): List<FolderMembershipPO>
    fun existsByFolderIdAndFeedId(folderId: UUID, feedId: UUID): Boolean
    fun deleteByFolderId(folderId: UUID)
    fun deleteAllByFolderIdIn(descendants: MutableList<UUID>)
}