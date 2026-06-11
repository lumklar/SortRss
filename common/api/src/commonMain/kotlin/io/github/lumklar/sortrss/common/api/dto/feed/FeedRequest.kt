package io.github.lumklar.sortrss.common.api.dto.feed

/**
 * 获取聚合rss信息请求
 */
data class FeedRequest(
    /**
     * api认证信息
     */
    var apiToken: String? = null
) {
}