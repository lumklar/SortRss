package io.github.lumklar.sortrss.common.api.dto

/**
 * Api 响应扩展信息（不对外暴露给前端，仅内部日志/监控/调试使用）
 * @param exceptionMsg 原生异常信息
 * @param stackTrace 异常堆栈（精简关键信息）
 */
data class ApiExtra(
    // 错误来源：前端核心判断字段
    val errorSource: ErrorSource? = null,

    /**
     * 错误码
     */
    val errorCode: Int? = null,
    /**
     * 错误信息
     */
    val errorMsg: String? = null,

    /**
     *原生异常堆栈信息
     */
    val stackTrace: String? = null,
    val exceptionMsg: String? = null,
)

/**
 * 错误来源枚举（前端直接根据这个值做差异化处理）
 */
enum class ErrorSource {
    /**
     * 领域/业务错误
     * */
    DOMAIN,

    /**
     * 系统/服务异常
     * */
    SYSTEM,

    /**
     * WEB层
     */
    WEB
}