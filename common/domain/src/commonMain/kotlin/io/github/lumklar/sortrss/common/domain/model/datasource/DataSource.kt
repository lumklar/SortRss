package io.github.lumklar.sortrss.common.domain.model.datasource

import io.github.lumklar.sortrss.common.domain.model.user.UserId
import kotlin.time.Instant

/**
 * 数据源聚合根。
 */
class DataSource private constructor(
    val id: DataSourceId,
    val userId: UserId,
    val type: DataSourceType,
    val name: DataSourceName,
    val connectionDetails: DataSourceConnectionDetails,
    initialLastSyncTime: Instant? = null
) {
    var lastSyncTime: Instant? = initialLastSyncTime
        private set

    companion object {
        /**
         * 创建一个新的数据源（首次创建，尚未同步）。
         */
        fun create(
            id: DataSourceId,
            userId: UserId,
            type: DataSourceType,
            name: String,
            connectionDetails: DataSourceConnectionDetails
        ): DataSource {
            val datasourceName = DataSourceName.fromString(name)
            // 业务校验：类型与连接详情匹配
            when (type) {
                DataSourceType.LOCAL_OPML -> require(connectionDetails is DataSourceConnectionDetails.LocalOpml) {
                    "Local OPML source must use LocalOpml connection details"
                }

                DataSourceType.FEVER_API -> require(connectionDetails is DataSourceConnectionDetails.FeverApi) {
                    "Fever API source requires FeverApi details"
                }

                DataSourceType.GOOGLE_READER_API -> require(connectionDetails is DataSourceConnectionDetails.GoogleReaderApi) {
                    "Google Reader API source requires GoogleReaderApi details"
                }
            }
            return DataSource(
                id = id,
                userId = userId,
                type = type,
                name = datasourceName,
                connectionDetails = connectionDetails,
                initialLastSyncTime = null   // 新数据源明确置为 null
            )
        }

        /**
         * 从持久化数据重建数据源（包含已有的订阅关联）。
         * @param lastSyncTime 从数据库读取的最后同步时间，若无则为 null
         */
        fun reconstruct(
            id: DataSourceId,
            userId: UserId,
            type: DataSourceType,
            name: String,
            connectionDetails: DataSourceConnectionDetails,
            lastSyncTime: Instant? = null   // 新增参数，默认为 null 保持兼容
        ): DataSource {
            val datasourceName = DataSourceName.fromString(name)
            return DataSource(
                id = id,
                userId = userId,
                type = type,
                name = datasourceName,
                connectionDetails = connectionDetails,
                initialLastSyncTime = lastSyncTime
            )
        }
    }

    // 提供一个更新方法，用于同步成功后更新该时间
    /**
     * 同步完成后调用，更新最后同步时间。
     */
    fun markSyncCompleted(syncTime: Instant) {
        // 可选业务校验：新时间不能早于旧时间（防止时钟回拨或乱序更新）
//        require(syncTime >= (lastSyncTime ?: Instant.MIN)) {
//            "New sync time cannot be earlier than last sync time"
//        }
        // 因为 setter 是 private，外部必须通过这个方法修改
        this.lastSyncTime = syncTime
    }
}