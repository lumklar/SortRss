package io.github.lumklar.sortrss.common.api.service

import io.github.lumklar.sortrss.common.api.annotation.ApiRoute
import io.github.lumklar.sortrss.common.api.annotation.HttpMethod
import io.github.lumklar.sortrss.common.api.dto.feed.FeedRequest
import io.github.lumklar.sortrss.common.api.route.FeedRoute

/**
 * 聚合rss相关接口
 */
interface FeedApi {
    /**
     * 获取聚合rss信息
     * @param request 请求参数
     * @return 聚合rss xml
     */
    @ApiRoute(FeedRoute.FEED_BASE, HttpMethod.GET)
    fun feed(request: FeedRequest): String;
}