package io.github.lumklar.sortrss.server.application.exception

import io.github.lumklar.sortrss.common.api.dto.ErrorSource

enum class ExceptionType {
    /**
     * 领域层异常
     */
    DOMAIN,
    /**
     * 应用层异常
     */
    APPLICATION,
    /**
     * 基础设施层异常
     */
    INFRASTRUCTURE,
    /**
     * 未知异常
     */
    UNKNOWN
}

/**
 * 应用层异常类型 → 契约层错误来源。
 * ErrorSource 只有 DOMAIN / SYSTEM / WEB：应用层与领域层的业务失败都归 DOMAIN，
 * 基础设施与未知异常归 SYSTEM；WEB 由参数校验等处理器直接指定。
 */
fun ExceptionType.toErrorSource(): ErrorSource = when (this) {
    ExceptionType.DOMAIN, ExceptionType.APPLICATION -> ErrorSource.DOMAIN
    ExceptionType.INFRASTRUCTURE, ExceptionType.UNKNOWN -> ErrorSource.SYSTEM
}
