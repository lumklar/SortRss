package io.github.lumklar.sortrss.common.domain.model.folder

import io.github.lumklar.sortrss.common.domain.model.datasource.DataSourceId
import io.github.lumklar.sortrss.common.domain.model.feed.Feed
import io.github.lumklar.sortrss.common.domain.model.feed.FeedId

/**
 * 文件夹聚合根。
 * 用于组织订阅源（Feed），属于特定数据源（DataSource）。
 * 只有本地订阅源（LOCAL_OPML）可以加入文件夹，远程订阅源不可移动。
 */
class Folder private constructor(
    val id: FolderId,
    val dataSourceId: DataSourceId,
    initialName: FolderName,                     // 构造函数参数，非属性
    private val feedIds: MutableList<FeedId> = mutableListOf()
) {
    // 公开可读，setter 私有，外部只能通过 rename 修改
    var name: FolderName = initialName
        private set

    // 便捷属性，直接返回字符串形式
    val displayName: String get() = name.value

    /** 该文件夹下的订阅源 ID 列表（只读） */
    val folderFeedIds: List<FeedId> get() = feedIds.toList()

    companion object {
        fun create(id: FolderId, dataSourceId: DataSourceId, name: String): Folder {
            return Folder(id, dataSourceId, FolderName.from(name))
        }

        fun reconstruct(
            id: FolderId,
            dataSourceId: DataSourceId,
            name: String,
            feedIds: List<FeedId>
        ): Folder {
            return Folder(id, dataSourceId, FolderName.from(name), feedIds.toMutableList())
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
        if (feedIds.any { it == feed.id }) throw FeedAlreadyInFolderException()
        feedIds.add(feed.id)
    }

    /**
     * 从文件夹中移除一个订阅源。
     */
    fun removeFeed(feedId: FeedId) {
        val removed = feedIds.removeAll { it == feedId }
        if (!removed) throw FeedNotInFolderException()
    }

    /**
     * 重命名文件夹。
     * 通过创建一个新的 FolderName 值对象来更新。
     */
    fun rename(newName: String) {
        name = FolderName.from(newName)
    }
}