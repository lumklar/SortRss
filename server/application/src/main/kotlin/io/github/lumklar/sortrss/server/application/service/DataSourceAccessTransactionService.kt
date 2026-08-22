package io.github.lumklar.sortrss.server.application.service

import io.github.lumklar.sortrss.common.domain.model.datasource.ValidatedConnectionDetails
import io.github.lumklar.sortrss.common.domain.service.DataSourceAccessResult
import io.github.lumklar.sortrss.common.domain.service.DataSourceAccessService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DataSourceAccessTransactionService(
    private val dataSourceAccessService: DataSourceAccessService
) {
    @Transactional
    suspend fun getOrCreateUserForRemoteDataSource(
        validated: ValidatedConnectionDetails
    ): DataSourceAccessResult {
        return dataSourceAccessService.getOrCreateUserForRemoteDataSource(validated)
    }
}