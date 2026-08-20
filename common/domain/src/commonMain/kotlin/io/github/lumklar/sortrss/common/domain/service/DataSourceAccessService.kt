package io.github.lumklar.sortrss.common.domain.service

import io.github.lumklar.sortrss.common.domain.model.datasource.*
import io.github.lumklar.sortrss.common.domain.model.user.*
import io.github.lumklar.sortrss.common.domain.shared.ability.IdGenerator
import io.github.lumklar.sortrss.common.domain.shared.enums.DataSourceType

class DataSourceAccessService(
    private val userIdGenerator: IdGenerator<UserId>,
    private val dataSourceIdGenerator: IdGenerator<DataSourceId>,
    private val userRepository: UserRepository,
    private val dataSourceRepository: DataSourceRepository,
    // 可以注入一个用于验证凭证的端口（如 OAuthClient 类似，但针对 Fever/GoogleReader）
    // private val dataSourceValidator: DataSourceValidator
) {

    /**
     * 根据远程数据源连接详情，获取或创建对应的用户（可能是匿名用户）。
     * 返回用户 ID 和数据源 ID，供后续使用。
     */
    fun getOrCreateUserForRemoteDataSource(
        type: DataSourceType,
        connectionDetails: DataSourceConnectionDetails
    ): DataSourceAccessResult {
        // 1. 验证凭证有效性（基础设施层实现，可抛出异常）
        // dataSourceValidator.validate(connectionDetails)

        // 2. 检查数据源是否已存在（仓储内部处理唯一性判断）
        val existingDataSource = dataSourceRepository.findByConnectionDetails(connectionDetails)
        if (existingDataSource != null) {
            // 已存在，直接返回其归属用户
            return DataSourceAccessResult(
                userId = existingDataSource.userId,
                dataSourceId = existingDataSource.id,
                isNewUser = false,
                isNewDataSource = false
            )
        }

        // 3. 不存在：创建匿名用户
        val newUserId = userIdGenerator.next()
        val user = User.registerAnonymous(newUserId)
        userRepository.save(user)

        // 4. 创建数据源并关联到匿名用户
        val newDataSourceId = dataSourceIdGenerator.next()
        val dataSource = DataSource.create(
            id = newDataSourceId,
            userId = newUserId,
            type = type,
            name = generateDefaultName(type, connectionDetails), // 可自定义
            connectionDetails = connectionDetails
        )
        dataSourceRepository.save(dataSource)

        return DataSourceAccessResult(
            userId = newUserId,
            dataSourceId = newDataSourceId,
            isNewUser = true,
            isNewDataSource = true
        )
    }

    private fun generateDefaultName(type: DataSourceType, details: DataSourceConnectionDetails): String {
        return when (type) {
            DataSourceType.LOCAL_OPML -> "本地订阅"
            DataSourceType.FEVER_API -> {
                val fever = details as DataSourceConnectionDetails.FeverApi
                "Fever (${fever.endpoint.value})"
            }

            DataSourceType.GOOGLE_READER_API -> {
                val gr = details as DataSourceConnectionDetails.GoogleReaderApi
                "Google Reader (${gr.endpoint.value})"
            }
        }
    }
}

data class DataSourceAccessResult(
    val userId: UserId,
    val dataSourceId: DataSourceId,
    val isNewUser: Boolean,
    val isNewDataSource: Boolean
)

