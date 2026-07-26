package io.github.lumklar.sortrss.common.domain.model.folder

import io.github.lumklar.sortrss.common.domain.model.feed.Feed
import io.github.lumklar.sortrss.common.domain.model.feed.FeedId
import io.github.lumklar.sortrss.common.domain.model.user.UserId

/**
 * 文件夹聚合根。
 * 用于组织订阅源（Feed），属于特定用户。
 * 只有本地订阅源（LOCAL_OPML）可以加入文件夹，远程订阅源不可移动。
 */
class Folder private constructor(
    val id: FolderId,
    val userId: UserId,
    private var name: String,
    private val feeds: MutableList<FolderFeed> = mutableListOf()
) {
    val displayName: String get() = name

    /** 该文件夹下的订阅源关联列表（只读） */
    val folderFeeds: List<FolderFeed> get() = feeds.toList()

    companion object {
        fun create(id: FolderId, userId: UserId, name: String): Folder {
            require(name.isNotBlank()) { throw FolderNameEmptyException() }
            return Folder(id, userId, name.trim())
        }

        fun reconstruct(id: FolderId, userId: UserId, name: String, feeds: List<FolderFeed>): Folder {
            return Folder(id, userId, name.trim(), feeds.toMutableList())
        }
    }

    /**
     * 将一个订阅源添加到该文件夹。
     * @param feed 要添加的订阅源
     * @throws CannotAddFeedToFolderException 如果订阅源不可编辑（非本地）
     * @throws FeedAlreadyInFolderException 如果订阅源已在文件夹中
     */
    fun addFeed(feed: Feed) {
        if (!feed.canEdit()) throw CannotAddFeedToFolderException()
        if (feeds.any { it.feedId == feed.id }) throw FeedAlreadyInFolderException()
        feeds.add(FolderFeed(folderId = id, feedId = feed.id))
    }

    /**
     * 从文件夹中移除一个订阅源。
     */
    fun removeFeed(feedId: FeedId) {
        val removed = feeds.removeAll { it.feedId == feedId }
        if (!removed) throw FeedNotInFolderException()
    }

    /**
     * 重命名文件夹。
     */
    fun rename(newName: String) {
        require(newName.isNotBlank()) { throw FolderNameEmptyException() }
        name = newName.trim()
    }
}