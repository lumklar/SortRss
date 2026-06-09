package io.github.lumklar.sortrss.common.api.dto

/**
 * 全局对外响应码
 * 前端/客户端唯一使用的错误码
 */
enum class ApiResultCode(
    val code: Int,
    val msg: String
) {
    /**
     *成功
     */
    SUCCESS(200, "操作成功"),

    /**
     *客户端错误
     */
    PARAM_ERROR(400, "请求参数错误"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "资源不存在"),

    // 业务错误
    BUSINESS_ERROR(422, "业务处理失败"),

    // 服务端/技术错误
    SERVER_ERROR(500, "服务器内部异常"),
    DB_ERROR(502, "数据库操作异常"),
    NETWORK_ERROR(503, "网络服务异常")
}

