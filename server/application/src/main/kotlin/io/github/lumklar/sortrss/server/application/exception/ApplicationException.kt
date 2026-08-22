package io.github.lumklar.sortrss.server.application.exception

import io.github.lumklar.sortrss.common.domain.shared.exception.DomainException

/**
 * 应用层异常，用于包装领域层异常，避免上层直接依赖领域异常
 */
class ApplicationException(
    val code: Int,
    message: String,
    type: ExceptionType = ExceptionType.UNKNOWN,
    cause: Throwable? = null
) : RuntimeException(message, cause) {

    companion object {
        /**
         * 从领域异常构建应用层异常
         * 注意：需要根据实际的 DomainErrorCode 结构调整 code 的获取方式
         */
        fun from(e: DomainException): ApplicationException {
            return ApplicationException(
                //TODO 设计应用层的异常代码
                code = e.domainCode.code,  // 假设 DomainErrorCode 有 code 属性
                message = e.message ?: e.domainCode.msg,
                type = ExceptionType.DOMAIN,
                cause = e
            )
        }
    }
}