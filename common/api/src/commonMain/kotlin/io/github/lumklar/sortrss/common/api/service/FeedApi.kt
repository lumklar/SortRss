package io.github.lumklar.sortrss.common.api.service

import io.github.lumklar.sortrss.common.api.annotation.ApiRoute
import io.github.lumklar.sortrss.common.api.annotation.HttpMethod
import io.github.lumklar.sortrss.common.api.dto.feed.FeverFeedRequest
import io.github.lumklar.sortrss.common.api.dto.feed.GoogleFeedRequest
import io.github.lumklar.sortrss.common.api.route.FeedRoute

/**
 * 聚合rss相关接口
 */
interface FeedApi {
    /**
     * 根据fever获取聚合rss信息
     * @param request 请求参数
     * @return 聚合rss xml
     */
    @ApiRoute(FeedRoute.FEED_FEVER, HttpMethod.GET)
    suspend fun feverFeed(request: FeverFeedRequest): String

    /**
     * 根据google feed获取聚合rss信息
     * @param request 请求参数
     * @return 聚合rss xml
     */
    @ApiRoute(FeedRoute.FEED_GOOGLE, HttpMethod.GET)
    suspend fun googleFeed(request: GoogleFeedRequest): String
}