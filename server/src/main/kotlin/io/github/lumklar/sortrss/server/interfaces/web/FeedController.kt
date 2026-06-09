package io.github.lumklar.sortrss.server.interfaces.web

import io.github.lumklar.sortrss.common.api.route.FeedRoute
import io.github.lumklar.sortrss.common.api.service.FeedApi
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class FeedController : FeedApi {
    @GetMapping(FeedRoute.FEED_BASE)
    override fun feed(): String {
        //订阅表，apiKey,apiType,apiUrl,apiUsername,id,token,isEnable
        //用户订阅关联表

        //订阅增删改查
        //用户增删改查
        //用户token管理
        //用户订阅源管理

        //该订阅不存在，创建一条订阅，同时生成对应token(可修改)
        return "{}";
    }
}
