package io.github.lumklar.sortrss.server.interfaces.web

import io.github.lumklar.sortrss.common.api.dto.feed.FeedRequest
import io.github.lumklar.sortrss.common.api.route.FeedRoute
import io.github.lumklar.sortrss.common.api.service.FeedApi
import io.github.lumklar.sortrss.common.domain.repository.UserRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

val logger = KotlinLogging.logger { }

@RestController
class FeedController(
    private val user: UserRepository
) : FeedApi {
    /**
     * TODO 移除GetMapping
     */
    @GetMapping(FeedRoute.FEED_BASE)
    override fun feed(request: FeedRequest): String {
        //订阅表，apiKey,apiType,apiUrl,apiUsername,id,token,isEnable
        //用户订阅关联表

        //订阅增删改查
        //用户增删改查
        //用户token管理
        //用户订阅源管理

        //该订阅不存在，创建一条订阅，同时生成对应token(可修改)
        logger.info { "收到请求" + request }
        return user.findById(1).toString()
//        return "{}";
    }
}
