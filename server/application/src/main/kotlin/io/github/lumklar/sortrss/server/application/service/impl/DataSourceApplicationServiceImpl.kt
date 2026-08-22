package io.github.lumklar.sortrss.server.application.service.impl

import io.github.lumklar.sortrss.common.domain.service.DataSourceAccessResult
import io.github.lumklar.sortrss.common.domain.service.DataSourceManagementService
import io.github.lumklar.sortrss.server.application.assembler.toDtoType
import io.github.lumklar.sortrss.server.application.pojo.datasource.command.GetOrCreateDataSourceCommand
import io.github.lumklar.sortrss.server.application.pojo.datasource.dto.DataSourceDTO
import io.github.lumklar.sortrss.server.application.service.DataSourceAccessTransactionService
import io.github.lumklar.sortrss.server.application.service.DataSourceApplicationService
import io.github.lumklar.sortrss.server.application.service.DataSourceConnectionFactory
import org.springframework.stereotype.Service
import kotlin.uuid.toJavaUuid

@Service
class DataSourceApplicationServiceImpl(
    private val  dataSourceConnectionFactory: DataSourceConnectionFactory,
    private val dataSourceManagementService: DataSourceManagementService,
    private val transactionService: DataSourceAccessTransactionService
) : DataSourceApplicationService {

    override suspend fun getOrCreateDataSource(command: GetOrCreateDataSourceCommand): DataSourceDTO {
        // 1. 通过工厂构建连接详情
        val details = dataSourceConnectionFactory.create(command.connection)
        val type = details.type

        // 2. 调用领域服务进行验证（无事务，通常网络请求）
        val validated = dataSourceManagementService.verifyConnection(details)

        // 3. 调用领域服务创建或更新数据源
        val result: DataSourceAccessResult = transactionService.getOrCreateUserForRemoteDataSource( validated)

        return DataSourceDTO(
            id = result.dataSourceId.value.toJavaUuid().toString(),
            userId = result.userId.value.toJavaUuid().toString(),
            type = type.toDtoType()
        )
    }
}