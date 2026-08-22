package io.github.lumklar.sortrss.common.api.dto.feed

/**
 * 数据源 Feed 请求的公共基类
 */
open class BaseFeedRequest(
    /** API 端点地址，例如 https://fever.example.com */
    open val endpoint: String,
    /** 期望的响应格式，默认使用 RSS */
    open val format: FeedFormat? = FeedFormat.RSS
)

/**
 * 支持的响应格式枚举
 */
enum class FeedFormat {
    /** Atom 1.0 格式 */
    ATOM,

    /** RSS 2.0 格式 */
    RSS,

    /** JSON 格式 */
    JSON
}

/**
 * Fever 数据源的 Feed 请求参数
 */
data class FeverFeedRequest(
    /** API 端点地址 */
    override val endpoint: String,
    /** 响应格式（可选，覆盖基类默认值） */
    override val format: FeedFormat? = null,
    /** Fever 登录用户名 */
    val username: String,
    /** Fever 登录密码 */
    val password: String
) : BaseFeedRequest(endpoint, format)

/**
 * Google Reader 风格数据源的 Feed 请求参数
 */
data class GoogleFeedRequest(
    /** API 端点地址 */
    override val endpoint: String,
    /** 响应格式（可选，覆盖基类默认值） */
    override val format: FeedFormat? = null,
    /** OAuth 访问令牌或 Google Reader 风格的认证凭据 */
    val accessToken: String
) : BaseFeedRequest(endpoint, format)