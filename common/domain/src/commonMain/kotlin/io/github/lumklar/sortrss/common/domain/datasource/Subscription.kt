// ==================== Subscription 实体 ====================
package io.github.lumklar.sortrss.common.domain.model.entity

import io.github.lumklar.sortrss.common.domain.shared.valueobject.*
import kotlin.time.Instant

/**
 * 用户的 Feed 订阅关系，归属于特定数据源。
 * 包含个性化配置（自定义名称、分组等）。
 */
class Subscription private constructor(
    val id: SubscriptionId,
    val userId: UserId,
    val dataSourceId: DataSourceId,
    val feedId: FeedId,
    var customName: String?,
    var groupId: GroupId?,
    var isActive: Boolean,
    var lastSyncedAt: Instant?                   // 上次同步时间
) {
    companion object {
        fun subscribe(
            userId: UserId,
            dataSourceId: DataSourceId,
            feedId: FeedId,
            customName: String? = null,
            groupId: GroupId? = null,
            id: SubscriptionId = SubscriptionId(0)
        ): Subscription = Subscription(id, userId, dataSourceId, feedId, customName?.trim(), groupId, true, null)
    }

    fun setCustomName(name: String?) { customName = name?.trim() }
    fun moveToGroup(groupId: GroupId?) { this.groupId = groupId }
    fun activate() { isActive = true }
    fun deactivate() { isActive = false }
    fun markSynced(timestamp: Instant) { lastSyncedAt = timestamp }
}