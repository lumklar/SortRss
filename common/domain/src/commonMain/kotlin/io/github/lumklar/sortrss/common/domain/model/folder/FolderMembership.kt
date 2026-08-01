package io.github.lumklar.sortrss.common.domain.model.folder

import io.github.lumklar.sortrss.common.domain.model.feed.FeedId
import io.github.lumklar.sortrss.common.domain.shared.enums.DataSourceType

/**
 * 代表“Feed 与 Folder 的关联”的聚合根。
 * 当前业务规则：一个 Feed 只能属于一个文件夹（单一归属）。
 * 将来可通过修改唯一约束和逻辑扩展为多对多。
 */
class FolderMembership private constructor(
    val id: FolderMembershipId,
    initialFolderId: FolderId,
    val feedId: FeedId,
    val dataSourceType: DataSourceType      // 冗余存储，便于快速校验
) {
    var folderId: FolderId = initialFolderId
        private set

    companion object {
        /**
         * 创建新的关联，仅允许本地订阅源（LOCAL_OPML）参与。
         * 调用方必须保证业务规则（如 Feed 未归属其他文件夹）已通过领域服务检查。
         */
        fun create(
            id: FolderMembershipId,
            folderId: FolderId,
            feedId: FeedId,
            dataSourceType: DataSourceType
        ): FolderMembership {
            require(dataSourceType == DataSourceType.LOCAL_OPML) {
                "Only LOCAL_OPML feeds can be added to a folder"
            }
            return FolderMembership(id, folderId, feedId, dataSourceType)
        }

        /**
         * 从持久化重建，不进行业务规则校验。
         */
        fun reconstruct(
            id: FolderMembershipId,
            folderId: FolderId,
            feedId: FeedId,
            dataSourceType: DataSourceType
        ): FolderMembership {
            return FolderMembership(id, folderId, feedId, dataSourceType)
        }
    }

    /**
     * 移动至另一个文件夹（只允许本地数据源）。
     * 本方法只负责自身状态修改，跨聚合规则由领域服务保证。
     */
    fun moveToFolder(newFolderId: FolderId) {
        folderId = newFolderId
    }
}
