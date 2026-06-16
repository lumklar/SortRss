package io.github.lumklar.sortrss.common.api.dto.feed

/**
 * 获取聚合rss信息请求
 */
data class FeedRequest(
    /**
     * api类型
     */
    var apiType: Int?,
    /**
     * api接口地址
     */
    var apiUrl: String?,
    /**
     * api用户名
     */
    var apiUsername: String?,
    /**
     * api认证信息
     */
    var apiToken: String?,
    ) {
}