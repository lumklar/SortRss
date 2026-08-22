package io.github.lumklar.sortrss.server.infrastructure.application

import io.github.lumklar.sortrss.common.domain.model.datasource.DataSourceConnectionDetails
import io.github.lumklar.sortrss.common.domain.shared.valueobjects.Url
import io.github.lumklar.sortrss.common.foundation.model.datasource.FeverApiConnectionDetails
import io.github.lumklar.sortrss.common.foundation.model.datasource.GoogleReaderApiConnectionDetails
import io.github.lumklar.sortrss.server.application.pojo.datasource.command.DataSourceConnectionCommand
import io.github.lumklar.sortrss.server.application.service.DataSourceConnectionFactory
import org.springframework.stereotype.Service
import kotlin.uuid.ExperimentalUuidApi

@Service
class DefaultDataSourceConnectionFactory : DataSourceConnectionFactory {

    @OptIn(ExperimentalUuidApi::class)
    override fun create(command: DataSourceConnectionCommand): DataSourceConnectionDetails {
        return when (command) {
            is DataSourceConnectionCommand.Fever -> {
                FeverApiConnectionDetails(
                    endpoint = Url.fromString(command.endpoint),
                    username = command.username,
                    password = command.password
                )
            }
            is DataSourceConnectionCommand.GoogleReader -> {
                GoogleReaderApiConnectionDetails(
                    endpoint = Url.fromString(command.endpoint),
                    accessToken = command.accessToken
                )
            }
        }
    }
}
