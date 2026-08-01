package io.github.lumklar.sortrss.server.infrastructure.persistence.folder.repository

import io.github.lumklar.sortrss.common.domain.model.feed.FeedId
import io.github.lumklar.sortrss.common.domain.model.folder.FolderId
import io.github.lumklar.sortrss.common.domain.model.folder.FolderMembership
import io.github.lumklar.sortrss.common.domain.model.folder.FolderMembershipId
import io.github.lumklar.sortrss.common.domain.model.folder.FolderMembershipRepository
import io.github.lumklar.sortrss.server.infrastructure.persistence.folder.convert.toDomain
import io.github.lumklar.sortrss.server.infrastructure.persistence.folder.convert.toPO
import io.github.lumklar.sortrss.server.infrastructure.persistence.folder.repository.jpa.JpaFolderMembershipRepository
import org.springframework.stereotype.Repository
import kotlin.uuid.toJavaUuid

@Repository
class FolderMembershipRepositoryImpl(
    private val jpaRepo: JpaFolderMembershipRepository
) : FolderMembershipRepository {

    override fun save(membership: FolderMembership) {
        jpaRepo.save(membership.toPO())
    }

    override fun delete(membership: FolderMembership) {
        jpaRepo.deleteById(membership.id.value.toJavaUuid())
    }

    override fun findById(id: FolderMembershipId): FolderMembership? {
        return jpaRepo.findById(id.value.toJavaUuid())
            .map { it.toDomain() }
            .orElse(null)
    }

    override fun findByFeedId(feedId: FeedId): FolderMembership? {
        return jpaRepo.findByFeedId(feedId.value.toJavaUuid())
            .map { it.toDomain() }
            .orElse(null)
    }

    override fun findByFolderId(folderId: FolderId): List<FolderMembership> {
        return jpaRepo.findByFolderId(folderId.value.toJavaUuid())
            .map { it.toDomain() }
    }

    override fun existsByFolderIdAndFeedId(folderId: FolderId, feedId: FeedId): Boolean {
        return jpaRepo.existsByFolderIdAndFeedId(folderId.value.toJavaUuid(), feedId.value.toJavaUuid())
    }
}
