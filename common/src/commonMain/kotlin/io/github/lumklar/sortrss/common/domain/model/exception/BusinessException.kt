package io.github.lumklar.sortrss.common.domain.model.exception

import io.github.lumklar.sortrss.common.domain.model.enums.DomainErrorCode

/**
 * 全局业务异常（DDD领域层标准异常）
 */
open class BusinessException(
    val domainCode: DomainErrorCode,
    message: String = domainCode.msg
) : RuntimeException(message) {

}
