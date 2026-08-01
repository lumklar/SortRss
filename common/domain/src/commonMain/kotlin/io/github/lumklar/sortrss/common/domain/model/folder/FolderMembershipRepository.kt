package io.github.lumklar.sortrss.common.domain.model.folder

import io.github.lumklar.sortrss.common.domain.model.feed.FeedId

interface FolderMembershipRepository {
    fun save(membership: FolderMembership)
    fun delete(membership: FolderMembership)
    fun findById(id: FolderMembershipId): FolderMembership?
    fun findByFeedId(feedId: FeedId): FolderMembership?   // 单一归属下直接返回最多一条
    fun findByFolderId(folderId: FolderId): List<FolderMembership>
    fun existsByFolderIdAndFeedId(folderId: FolderId, feedId: FeedId): Boolean
}
