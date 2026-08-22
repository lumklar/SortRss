package io.github.lumklar.sortrss.common.domain.service

import io.github.lumklar.sortrss.common.domain.model.datasource.*
import io.github.lumklar.sortrss.common.domain.model.user.*
import io.github.lumklar.sortrss.common.domain.shared.ability.IdGenerator
import io.github.lumklar.sortrss.common.domain.shared.ability.datasource.FeedServiceFactory
import io.github.lumklar.sortrss.common.domain.shared.enums.DataSourceType

class DataSourceManagementService(
    private val feedServiceFactory: FeedServiceFactory,
    private val dataSourceRepository: DataSourceRepository,
    private val userRepository: UserRepository,   // 新增依赖
    private val idGenerator: IdGenerator<DataSourceId>
) {
    /**
     * 验证连接，并返回已验证的值对象。
     * @throws DataSourceConnectionException 如果验证失败
     */
    suspend fun verifyConnection(connectionDetails: DataSourceConnectionDetails): ValidatedConnectionDetails {
        require(connectionDetails.type.isRemote()) { "仅支持远程数据源" }
        val feedService = feedServiceFactory.create(connectionDetails)
        feedService.validateConnection()  // 失败会抛出异常
        return ValidatedConnectionDetails.of(connectionDetails)
    }

    /**
     * 创建数据源（适用于所有类型）。
     * 对于远程数据源，会检查 identityKey 是否已存在，避免重复添加。
     * 本地数据源不进行全局唯一性检查，允许重复创建。
     * 额外规则：匿名用户（ANONYMOUS）只能拥有一个数据源。
     */
    fun createDataSource(
        userId: UserId,
        type: DataSourceType,
        name: String,
        validatedConnectionDetails: ValidatedConnectionDetails
    ): DataSource {
        // 检查连接详情类型是否匹配
        require(type == validatedConnectionDetails.type) { "数据源类型不匹配" }

        // 全局唯一性检查（仅远程数据源）
        val connectionDetails = validatedConnectionDetails.delegate
        if (dataSourceRepository.existsByConnectionDetails(connectionDetails)) {
            throw DataSourceAlreadyExistsException()
        }

        // 匿名用户单数据源限制
        val user = userRepository.findById(userId)
            ?: throw UserNotFoundException()
        if (user.registrationSource == RegistrationSource.ANONYMOUS) {
            val existingDataSources = dataSourceRepository.findByUserId(userId)
            if (existingDataSources.isNotEmpty()) {
                throw AnonymousUserDataSourceLimitException()
            }
        }

        val dataSource = DataSource.create(
            id = idGenerator.next(),
            userId = userId,
            type = type,
            name = name,
            connectionDetails = connectionDetails
        )
        return dataSourceRepository.save(dataSource)
    }
}
