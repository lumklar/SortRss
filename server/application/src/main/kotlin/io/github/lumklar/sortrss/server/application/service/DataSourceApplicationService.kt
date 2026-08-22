package io.github.lumklar.sortrss.server.application.service

import io.github.lumklar.sortrss.server.application.pojo.datasource.command.GetOrCreateDataSourceCommand
import io.github.lumklar.sortrss.server.application.pojo.datasource.dto.DataSourceDTO

interface DataSourceApplicationService {
    suspend fun getOrCreateDataSource(command: GetOrCreateDataSourceCommand): DataSourceDTO
}
