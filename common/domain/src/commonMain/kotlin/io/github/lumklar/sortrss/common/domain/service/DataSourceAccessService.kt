package io.github.lumklar.sortrss.common.domain.service

import io.github.lumklar.sortrss.common.domain.model.datasource.DataSourceConnectionDetails
import io.github.lumklar.sortrss.common.domain.model.datasource.DataSourceId
import io.github.lumklar.sortrss.common.domain.model.datasource.DataSourceRepository
import io.github.lumklar.sortrss.common.domain.model.datasource.ValidatedConnectionDetails
import io.github.lumklar.sortrss.common.domain.model.user.*
import io.github.lumklar.sortrss.common.domain.shared.ability.IdGenerator
import io.github.lumklar.sortrss.common.domain.shared.enums.DataSourceType

class DataSourceAccessService(
    private val userIdGenerator: IdGenerator<UserId>,
    private val userRepository: UserRepository,
    private val dataSourceRepository: DataSourceRepository,
    private val dataSourceManagementService: DataSourceManagementService
) {
    /**
     * 根据远程数据源连接详情，获取或创建对应的匿名用户。
     */
    fun getOrCreateUserForRemoteDataSource(
        validatedConnectionDetails: ValidatedConnectionDetails
    ): DataSourceAccessResult {
        // 0. 防御性检查：仅允许远程数据源
        val type = validatedConnectionDetails.type
        require(type.isRemote()) { "仅支持远程数据源，当前类型: $type" }

        // 1. 检查数据源是否已存在
        val connectionDetails = validatedConnectionDetails.delegate
        val existingDataSource = dataSourceRepository.findByConnectionDetails(connectionDetails)
        if (existingDataSource != null) {

            val existingUser = userRepository.findById(existingDataSource.userId)
                ?: throw IllegalStateException("数据源关联的用户不存在: ${existingDataSource.userId}")

            // 非匿名用户不允许通过远程数据源连接详情直接登录
            if (existingUser.registrationSource != RegistrationSource.ANONYMOUS) {
                throw DataSourceAccessDeniedException("该数据源已绑定非匿名用户，不允许通过远程数据源连接详情直接登录")
            }

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

        // 3. 使用 DataSourceService 创建数据源（统一入口）
        val dataSource = dataSourceManagementService.createDataSource(
            userId = newUserId,
            type = type,
            name = generateDefaultName(type, connectionDetails),
            validatedConnectionDetails = validatedConnectionDetails
        )

        return DataSourceAccessResult(
            userId = newUserId,
            dataSourceId = dataSource.id,
            isNewUser = true,
            isNewDataSource = true
        )
    }

    private fun generateDefaultName(type: DataSourceType, details: DataSourceConnectionDetails): String {
        return when (type) {
            DataSourceType.LOCAL_OPML -> "本地订阅"
            DataSourceType.FEVER_API -> "Fever 订阅"
            DataSourceType.GOOGLE_READER_API -> "Google Reader 订阅"
        }
    }
}

data class DataSourceAccessResult(
    val userId: UserId,
    val dataSourceId: DataSourceId,
    val isNewUser: Boolean,
    val isNewDataSource: Boolean
)

