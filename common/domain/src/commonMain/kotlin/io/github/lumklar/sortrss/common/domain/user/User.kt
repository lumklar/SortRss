// ==================== 用户聚合根 ====================
package io.github.lumklar.sortrss.common.domain.model.entity

import io.github.lumklar.sortrss.common.domain.shared.ability.PasswordEncoder
import io.github.lumklar.sortrss.common.domain.shared.valueobject.*
import io.github.lumklar.sortrss.common.domain.userentry.UserEntry

/**
 * 用户聚合根。
 * 拥有数据源、分组（归属数据源）、订阅关系、文章阅读状态。
 */
class User private constructor(
    val id: UserId,
    val username: String,
    private var passwordHash: String,

    // 内部集合，维护一致性
    private val dataSources: MutableList<DataSource> = mutableListOf(),
    private val userEntries: MutableList<UserEntry> = mutableListOf()
) {
    companion object {
        const val MIN_PASSWORD_LENGTH = 8

        /** 注册新用户 */
        fun register(
            username: String,
            plainPassword: String,
            passwordEncoder: PasswordEncoder,
            id: UserId = UserId(0)
        ): User {
            require(username.isNotBlank()) { "用户名不能为空" }
            require(plainPassword.length >= MIN_PASSWORD_LENGTH) {
                "密码长度不能小于 $MIN_PASSWORD_LENGTH"
            }
            val hash = passwordEncoder.encode(plainPassword)
            return User(id, username.trim(), hash)
        }

        /** 从持久化重建 */
        fun fromPersistence(
            id: Long,
            username: String,
            passwordHash: String
        ): User {
            require(username.isNotBlank()) { "用户名不能为空" }
            return User(UserId(id), username.trim(), passwordHash)
        }
    }

    // ---------- 密码 ----------
    fun verifyPassword(plain: String, encoder: PasswordEncoder): Boolean =
        encoder.matches(plain, passwordHash)

    fun changePassword(oldPlain: String, newPlain: String, encoder: PasswordEncoder) {
        require(verifyPassword(oldPlain, encoder)) { "原密码错误" }
        require(newPlain.length >= MIN_PASSWORD_LENGTH) { "新密码长度不能小于 $MIN_PASSWORD_LENGTH" }
        passwordHash = encoder.encode(newPlain)
    }

    // ---------- 数据源管理 ----------
    fun addDataSource(
        type: DataSourceType,
        name: String,
        url: String? = null,
        credentials: String? = null
    ): DataSource {
        val ds = DataSource.create(
            userId = id,
            type = type,
            name = name,
            url = url,
            credentials = credentials,
            id = DataSourceId(0) // 由 repository 分配
        )
        dataSources.add(ds)
        return ds
    }

    fun removeDataSource(dataSourceId: DataSourceId) {
        dataSources.removeAll { it.id == dataSourceId }
        // 级联删除该数据源下的所有分组和订阅（已在 DataSource 内部维护）
    }

    fun getDataSources(): List<DataSource> = dataSources.toList()

    fun getDataSource(dataSourceId: DataSourceId): DataSource? =
        dataSources.firstOrNull { it.id == dataSourceId }

    // ---------- 分组管理（归属数据源） ----------
    fun createGroup(dataSourceId: DataSourceId, name: String, displayOrder: Int = 0): Group {
        val ds = getDataSource(dataSourceId) ?: throw IllegalArgumentException("数据源不存在")
        return ds.addGroup(name, displayOrder)
    }

    fun removeGroup(dataSourceId: DataSourceId, groupId: GroupId) {
        val ds = getDataSource(dataSourceId) ?: throw IllegalArgumentException("数据源不存在")
        ds.removeGroup(groupId)
    }

    /** 获取用户所有分组（合并所有数据源的分组） */
    fun getAllGroups(): List<Group> = dataSources.flatMap { it.getGroups() }

    /** 获取指定分组（从所有数据源中查找） */
    fun findGroup(groupId: GroupId): Group? =
        dataSources.flatMap { it.getGroups() }.firstOrNull { it.id == groupId }

    // ---------- 订阅管理 ----------
    fun subscribeFeed(
        dataSourceId: DataSourceId,
        feed: Feed,
        customName: String? = null,
        groupId: GroupId? = null
    ): Subscription {
        val ds = getDataSource(dataSourceId) ?: throw IllegalArgumentException("数据源不存在")
        return ds.subscribe(feed, customName, groupId)
    }

    fun unsubscribe(subscriptionId: SubscriptionId) {
        dataSources.forEach { it.removeSubscription(subscriptionId) }
    }

    fun getSubscriptions(): List<Subscription> = dataSources.flatMap { it.getSubscriptions() }

    // ---------- 阅读状态管理 ----------
    fun markEntryRead(entryId: EntryId): UserEntry {
        val ue = userEntries.find { it.entryId == entryId }
        if (ue != null) {
            ue.markRead()
            return ue
        } else {
            val newUe = UserEntry.create(id, entryId, isRead = true)
            userEntries.add(newUe)
            return newUe
        }
    }

    fun markEntryUnread(entryId: EntryId) {
        userEntries.find { it.entryId == entryId }?.markUnread()
    }

    fun toggleStar(entryId: EntryId): UserEntry {
        val ue = userEntries.find { it.entryId == entryId }
        if (ue != null) {
            ue.toggleStar()
            return ue
        } else {
            val newUe = UserEntry.create(id, entryId, isStarred = true)
            userEntries.add(newUe)
            return newUe
        }
    }

    fun getUserEntry(entryId: EntryId): UserEntry? = userEntries.find { it.entryId == entryId }

    /** 合并远程阅读状态（已读优先） */
    fun mergeReadStatus(entryId: EntryId, remoteIsRead: Boolean): Boolean {
        val ue = userEntries.find { it.entryId == entryId }
        if (ue != null) {
            return ue.mergeReadStatus(remoteIsRead)
        } else if (remoteIsRead) {
            // 远端已读，但本地没有记录，自动创建已读
            userEntries.add(UserEntry.create(id, entryId, isRead = true))
            return true
        }
        return false
    }
}