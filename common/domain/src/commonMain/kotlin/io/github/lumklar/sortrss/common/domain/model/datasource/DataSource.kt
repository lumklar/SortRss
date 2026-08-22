package io.github.lumklar.sortrss.common.domain.model.datasource

import io.github.lumklar.sortrss.common.domain.model.user.UserId
import io.github.lumklar.sortrss.common.domain.shared.enums.DataSourceType
import kotlin.time.Clock
import kotlin.time.Instant

/**
 * 数据源聚合根。
 */
class DataSource private constructor(
    val id: DataSourceId,
    val userId: UserId,
    val type: DataSourceType,
    val name: DataSourceName,
    connectionDetails: DataSourceConnectionDetails,
    lastSyncTime: Instant? = null,
    lastAllReadAt: Instant? = null   // 新增：用户全部已读时间戳
) {
    var connectionDetails: DataSourceConnectionDetails = connectionDetails
        private set

    var lastSyncTime: Instant? = lastSyncTime
        private set

    var lastAllReadAt: Instant? = lastAllReadAt   // 新增字段，私有 set
        private set

    companion object {
        /**
         * 创建一个新的数据源（首次创建，尚未同步）。
         */
        internal fun create(
            id: DataSourceId,
            userId: UserId,
            type: DataSourceType,
            name: String,
            connectionDetails: DataSourceConnectionDetails
        ): DataSource {
            val datasourceName = DataSourceName.fromString(name)

            // 类型匹配校验（由具体实现保证）
            require(type == connectionDetails.type) {
                "Data source type mismatch: expected ${type}, got ${connectionDetails.type}"
            }

            return DataSource(
                id = id,
                userId = userId,
                type = type,
                name = datasourceName,
                connectionDetails = connectionDetails,
                lastSyncTime = null,
                lastAllReadAt = null   // 新数据源明确置为 null
            )
        }

        /**
         * 从持久化数据重建数据源（包含已有的订阅关联）。
         * @param lastSyncTime 从数据库读取的最后同步时间，若无则为 null
         * @param lastAllReadAt 从数据库读取的用户全部已读时间，若无则为 null（新增）
         */
        fun reconstruct(
            id: DataSourceId,
            userId: UserId,
            type: DataSourceType,
            name: String,
            connectionDetails: DataSourceConnectionDetails,
            lastSyncTime: Instant? = null,
            lastAllReadAt: Instant? = null
        ): DataSource {
            val datasourceName = DataSourceName.fromString(name)
            return DataSource(
                id = id,
                userId = userId,
                type = type,
                name = datasourceName,
                connectionDetails = connectionDetails,
                lastSyncTime = lastSyncTime,
                lastAllReadAt = lastAllReadAt
            )
        }
    }

    /**
     * 更新连接详情（通常用于密码等敏感信息变更）。
     * 约束：
     * - 类型必须匹配；
     * - identityKey 不可变（即 endpoint 和 username 不能变，否则应视为新数据源）。
     */
    fun updateConnectionDetails(newDetails: DataSourceConnectionDetails) {
        require(newDetails.type == type) {
            "Data source type mismatch: expected $type, got ${newDetails.type}"
        }
        require(newDetails.hasSameIdentityAs(connectionDetails)) {
            "Cannot change identity key: ${connectionDetails.identityKey} -> ${newDetails.identityKey}"
        }

        this.connectionDetails = newDetails
    }

    /**
     * 更新最后同步时间为当前时刻。
     */
    fun markSynced() {
        markSyncCompleted(Clock.System.now())
    }

    /**
     * 同步完成后调用，更新最后同步时间。
     */
    fun markSyncCompleted(syncTime: Instant) {
        require(syncTime >= (this@DataSource.lastSyncTime ?: syncTime)) {
            "Sync time cannot be earlier than last sync time: ${this@DataSource.lastSyncTime}"
        }
        this.lastSyncTime = syncTime
    }

    /**
     * 用户将数据源下所有文章标记为已读时调用，更新全部已读时间戳。
     */
    fun markAllRead(readTime: Instant) {
        // 可选：确保新时间不早于旧时间（如需要可放开注释）
        // require(readTime >= (lastAllReadAt ?: Instant.MIN)) { "New all-read time cannot be earlier than previous" }
        this.lastAllReadAt = readTime
    }

}