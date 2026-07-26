package io.github.lumklar.sortrss.common.domain.shared.exception

import io.github.lumklar.sortrss.common.domain.shared.error.DomainErrorCode


/**
 * 全局业务异常（DDD领域层标准异常）
 */
open class DomainException(
    val domainCode: DomainErrorCode,
    message: String = domainCode.msg
) : RuntimeException(message) {

}
