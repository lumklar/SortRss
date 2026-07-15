// ==================== UserEntry 实体（用户文章状态） ====================
package io.github.lumklar.sortrss.common.domain.model.entity

import io.github.lumklar.sortrss.common.domain.shared.valueobject.EntryId
import io.github.lumklar.sortrss.common.domain.shared.valueobject.UserEntryId
import io.github.lumklar.sortrss.common.domain.shared.valueobject.UserId
import kotlin.time.Clock
import kotlin.time.Instant

/**
 * 用户对某篇文章的阅读/收藏状态。
 * 全局统一，不区分数据源。
 */
class UserEntry private constructor(
    val id: UserEntryId,
    val userId: UserId,
    val entryId: EntryId,
    var isRead: Boolean,
    var isStarred: Boolean,
    var readAt: Instant?,              // 阅读时间
    var lastModifiedAt: Instant        // 状态最后变更时间
) {
    companion object {
        /** 创建新的用户文章状态，使用当前时间作为最后修改时间 */
        fun create(
            userId: UserId,
            entryId: EntryId,
            isRead: Boolean = false,
            isStarred: Boolean = false,
            id: UserEntryId = UserEntryId(0)
        ): UserEntry {
            val now = Clock.System.now()
            return UserEntry(id, userId, entryId, isRead, isStarred, if (isRead) now else null, now)
        }
    }

    /** 本地标记已读 */
    fun markRead() {
        if (!isRead) {
            isRead = true
            val now = Clock.System.now()
            readAt = now
            lastModifiedAt = now
        }
    }

    /** 本地标记未读 */
    fun markUnread() {
        if (isRead) {
            isRead = false
            readAt = null
            lastModifiedAt = Clock.System.now()
        }
    }

    /** 切换收藏 */
    fun toggleStar() {
        isStarred = !isStarred
        lastModifiedAt = Clock.System.now()
    }

    /**
     * 合并远程阅读状态（已读优先原则）。
     * 若远程为已读，则本地必定设为已读；若远程为未读，不改变本地已读状态。
     * 返回是否发生变更。
     */
    fun mergeReadStatus(remoteIsRead: Boolean): Boolean {
        if (remoteIsRead && !isRead) {
            isRead = true
            val now = Clock.System.now()
            readAt = now
            lastModifiedAt = now
            return true
        }
        return false
    }

    /**
     * 带时间戳的合并，可用于更精细控制。
     * 仍遵守已读优先：只要任意源为已读就保留已读，只有在本地未读且远程已读时才覆盖。
     * 若远程为未读，即使时间戳更新也不回退。
     */
    fun mergeReadStatusWithTimestamp(remoteIsRead: Boolean, remoteTimestamp: Instant): Boolean {
        if (!remoteIsRead) return false
        if (isRead) return false
        // 远程已读，且本地未读，直接覆盖
        isRead = true
        readAt = remoteTimestamp
        lastModifiedAt = remoteTimestamp
        return true
    }
}