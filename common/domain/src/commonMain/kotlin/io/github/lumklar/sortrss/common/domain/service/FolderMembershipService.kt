package io.github.lumklar.sortrss.common.domain.service

import io.github.lumklar.sortrss.common.domain.model.feed.FeedId
import io.github.lumklar.sortrss.common.domain.model.feed.FeedNotFoundException
import io.github.lumklar.sortrss.common.domain.model.feed.FeedRepository
import io.github.lumklar.sortrss.common.domain.model.folder.*
import io.github.lumklar.sortrss.common.domain.shared.ability.IdGenerator
import io.github.lumklar.sortrss.common.domain.shared.enums.DataSourceType

class FolderMembershipService(
    private val membershipIdGenerator: IdGenerator<FolderMembershipId>,
    private val membershipRepo: FolderMembershipRepository,
    private val folderRepo: FolderRepository,
    private val feedRepo: FeedRepository
) {
    /**
     * 将 Feed 添加到一个文件夹（单一归属）。
     * @throws CannotAddFeedToFolderException 如果 Feed 不是本地订阅源或已在其他文件夹中
     * @throws FeedAlreadyInFolderException 如果 Feed 已在目标文件夹中
     */
    fun addFeedToFolder(feedId: FeedId, folderId: FolderId) {
        val feed = feedRepo.findById(feedId) ?: throw FeedNotFoundException()
        val folder = folderRepo.findById(folderId) ?: throw FolderNotFoundException()

        // 规则1: 只有本地数据源 Feed 才能加入文件夹
        if (feed.sourceType != DataSourceType.LOCAL_OPML) {
            throw CannotAddFeedToFolderException("Only local feeds can be added to a folder")
        }

        // 规则2: Feed 当前不得属于任何文件夹（单一归属）
        val existingMembership = membershipRepo.findByFeedId(feedId)
        if (existingMembership != null) {
            throw FeedAlreadyInFolderException("Feed is already in folder '${existingMembership.folderId}'")
        }

        // 规则3: 目标文件夹的数据源类型必须兼容（LOCAL_OPML）
        if (folder.dataSourceType != DataSourceType.LOCAL_OPML) {
            throw CannotAddFeedToFolderException("Target folder must be LOCAL_OPML type")
        }

        // 规则4: 不可在同一文件夹重复添加（若未来唯一约束由数据库保证，此步可省）
        if (membershipRepo.existsByFolderIdAndFeedId(folderId, feedId)) {
            throw FeedAlreadyInFolderException("Feed already exists in this folder")
        }

        val membership = FolderMembership.create(
            id = membershipIdGenerator.next(),
            folderId = folderId,
            feedId = feedId,
            dataSourceType = feed.sourceType
        )
        membershipRepo.save(membership)
    }

    /**
     * 将 Feed 从一个文件夹移动到另一个文件夹。
     * 只允许移动本地数据源的 Feed，且目标文件夹也必须是 LOCAL_OPML。
     */
    fun moveFeedToFolder(feedId: FeedId, targetFolderId: FolderId) {
        val feed = feedRepo.findById(feedId) ?: throw FeedNotFoundException()
        val targetFolder = folderRepo.findById(targetFolderId) ?: throw FolderNotFoundException()

        // 规则1: 只有本地 Feed 可移动
        if (feed.sourceType != DataSourceType.LOCAL_OPML) {
            throw CannotMoveFeedException("Only local feeds can be moved")
        }

        // 规则2: 目标文件夹必须为 LOCAL_OPML
        if (targetFolder.dataSourceType != DataSourceType.LOCAL_OPML) {
            throw CannotMoveFeedException("Target folder must be LOCAL_OPML type")
        }

        val currentMembership = membershipRepo.findByFeedId(feedId)
            ?: throw FeedNotInAnyFolderException("Feed is not in any folder, please add it first")

        // 规则3: 不能移动到同一个文件夹
        if (currentMembership.folderId == targetFolderId) {
            throw CannotMoveFeedException("Feed is already in this folder")
        }

        // 移动操作：直接修改现有关联对象
        currentMembership.moveToFolder(targetFolderId)
        membershipRepo.save(currentMembership)
    }

    /**
     * 将 Feed 从当前文件夹中移除（取消归属）。
     */
    fun removeFeedFromFolder(feedId: FeedId) {
        val feed = feedRepo.findById(feedId) ?: throw FeedNotFoundException()
        if (feed.sourceType != DataSourceType.LOCAL_OPML) {
            throw CannotRemoveFeedException("Only local feeds can be removed from folders")
        }

        val membership = membershipRepo.findByFeedId(feedId)
            ?: throw FeedNotInAnyFolderException("Feed is not in any folder")
        membershipRepo.delete(membership)
    }
}
