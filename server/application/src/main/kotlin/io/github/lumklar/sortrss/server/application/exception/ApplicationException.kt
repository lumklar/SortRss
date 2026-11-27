package io.github.lumklar.sortrss.server.application.exception

import io.github.lumklar.sortrss.common.domain.shared.error.DomainErrorCode
import io.github.lumklar.sortrss.common.domain.shared.exception.DomainException

/**
 * 应用层异常，用于包装领域层异常，避免上层直接依赖领域异常。
 *
 * 对外错误码使用应用层的 [errorCode]；领域码只保存在 [domainCode] 里用于日志溯源，
 * 不再透传到 `ApiExtra.errorCode`。
 */
class ApplicationException(
    val errorCode: ApplicationErrorCode = ApplicationErrorCode.UNKNOWN,
    message: String = errorCode.msg,
    val type: ExceptionType = ExceptionType.UNKNOWN,
    val domainCode: DomainErrorCode? = null,
    cause: Throwable? = null
) : RuntimeException(message, cause) {

    /** 对外暴露的数值码，保持与旧调用方（`e.code`）的兼容 */
    val code: Int get() = errorCode.code

    /** 领域码的数值形式，供上层日志溯源；避免上层直接依赖 DomainErrorCode 类型 */
    val domainCodeValue: Int? get() = domainCode?.code

    companion object {
        /**
         * 从领域异常构建应用层异常。
         * 领域码仅作为溯源信息保留，对外错误码统一为 [ApplicationErrorCode.DOMAIN_REJECTED]。
         */
        fun from(e: DomainException): ApplicationException = ApplicationException(
            errorCode = ApplicationErrorCode.DOMAIN_REJECTED,
            message = e.message ?: e.domainCode.msg,
            type = ExceptionType.DOMAIN,
            domainCode = e.domainCode,
            cause = e
        )
    }
}
