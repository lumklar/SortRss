// ==================== DataSource 实体 ====================
package io.github.lumklar.sortrss.common.domain.model.entity

import io.github.lumklar.sortrss.common.domain.shared.valueobject.*

/**
 * 用户的数据源（本地 OPML / 远程 Fever / Google Reader 等）。
 * 拥有自己的分组和订阅列表。
 */
class DataSource private constructor(
    val id: DataSourceId,
    val userId: UserId,
    val type: DataSourceType,
    var name: String,
    var url: String?,
    var credentials: String?,

    private val groups: MutableList<Group> = mutableListOf(),
    private val subscriptions: MutableList<Subscription> = mutableListOf()
) {
    companion object {
        fun create(
            userId: UserId,
            type: DataSourceType,
            name: String,
            url: String? = null,
            credentials: String? = null,
            id: DataSourceId = DataSourceId(0)
        ): DataSource {
            require(name.isNotBlank()) { "数据源名称不能为空" }
            if (type.isRemote()) require(!url.isNullOrBlank()) { "远程数据源必须提供 URL" }
            return DataSource(id, userId, type, name.trim(), url?.trim(), credentials)
        }
    }

    fun updateInfo(name: String, url: String?, credentials: String?) {
        require(name.isNotBlank()) { "数据源名称不能为空" }
        this.name = name.trim()
        this.url = url?.trim()
        this.credentials = credentials
    }

    // ---------- 分组 ----------
    fun addGroup(name: String, displayOrder: Int = 0): Group {
        // 实际 ID 由 repository 分配，这里用 0 占位
        val group = Group.create(id, name, displayOrder)
        groups.add(group)
        return group
    }

    fun removeGroup(groupId: GroupId) {
        groups.removeAll { it.id == groupId }
        // 同时将该分组下的订阅置空分组
        subscriptions.filter { it.groupId == groupId }.forEach { it.moveToGroup(null) }
    }

    fun getGroups(): List<Group> = groups.toList()

    // ---------- 订阅 ----------
    fun subscribe(feed: Feed, customName: String? = null, groupId: GroupId? = null): Subscription {
        // 检查该数据源下是否已订阅该 Feed
        if (subscriptions.any { it.feedId == feed.id }) {
            throw IllegalStateException("该数据源下已订阅此 Feed")
        }
        val sub = Subscription.subscribe(userId, id, feed.id, customName, groupId)
        subscriptions.add(sub)
        return sub
    }

    fun removeSubscription(subscriptionId: SubscriptionId) {
        subscriptions.removeAll { it.id == subscriptionId }
    }

    fun getSubscriptions(): List<Subscription> = subscriptions.toList()
}