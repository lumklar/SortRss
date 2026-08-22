package io.github.lumklar.sortrss.server.application.exception

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