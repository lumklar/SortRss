package io.github.lumklar.sortrss.common.api.service

import io.github.lumklar.sortrss.common.api.annotation.ApiRoute
import io.github.lumklar.sortrss.common.api.annotation.HttpMethod
import io.github.lumklar.sortrss.common.api.route.FeedRoute

interface FeedApi {
    /**
     * 获取feed
     */
    @ApiRoute(FeedRoute.FEED_BASE, HttpMethod.GET)
    fun feed(): String;
}