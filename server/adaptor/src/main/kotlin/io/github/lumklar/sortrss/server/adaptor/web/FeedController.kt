package io.github.lumklar.sortrss.server.adaptor.web

import io.github.lumklar.sortrss.common.api.dto.feed.FeedRequest
import io.github.lumklar.sortrss.common.api.route.FeedRoute
import io.github.lumklar.sortrss.common.api.service.FeedApi
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

val logger = KotlinLogging.logger { }

@RestController
class FeedController(
) : FeedApi {
    /**
     * TODO 移除GetMapping
     */
    @GetMapping(FeedRoute.FEED_BASE)
    override fun feed(request: FeedRequest): String {
        //判断是否存在
        //不存在，注册用户
        //注册datasource

        logger.info { "收到请求" + request }
        return "1"
//        return "{}";
    }
}
