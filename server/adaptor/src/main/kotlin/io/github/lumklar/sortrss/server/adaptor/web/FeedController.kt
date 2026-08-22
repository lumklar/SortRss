package io.github.lumklar.sortrss.server.adaptor.web

import io.github.lumklar.sortrss.common.api.dto.feed.FeverFeedRequest
import io.github.lumklar.sortrss.common.api.dto.feed.GoogleFeedRequest
import io.github.lumklar.sortrss.common.api.route.FeedRoute
import io.github.lumklar.sortrss.common.api.service.FeedApi
import io.github.lumklar.sortrss.server.application.pojo.datasource.command.DataSourceConnectionCommand
import io.github.lumklar.sortrss.server.application.pojo.datasource.command.GetOrCreateDataSourceCommand
import io.github.lumklar.sortrss.server.application.service.DataSourceApplicationService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RestController

val logger = KotlinLogging.logger { }

@RestController
class FeedController(
    private val dataSourceApplicationService: DataSourceApplicationService
) : FeedApi {
    /**
     * TODO 移除GetMapping
     */
    @GetMapping(FeedRoute.FEED_FEVER)
    override suspend fun feverFeed(
        @ModelAttribute request: FeverFeedRequest
    ): String {
        val command = DataSourceConnectionCommand.Fever(request.endpoint, request.username, request.password)
        return buildResponse(command)
    }

    @GetMapping(FeedRoute.FEED_GOOGLE)
    override suspend fun googleFeed(
        @ModelAttribute request: GoogleFeedRequest
    ): String {
        val command = DataSourceConnectionCommand.GoogleReader(request.endpoint, request.accessToken)
        return buildResponse(command)
    }

    private suspend fun buildResponse(command: DataSourceConnectionCommand): String {
        val dto = dataSourceApplicationService.getOrCreateDataSource(
            GetOrCreateDataSourceCommand(connection = command)
        )
        return "创建成功，id为：" + dto.id
    }

}
