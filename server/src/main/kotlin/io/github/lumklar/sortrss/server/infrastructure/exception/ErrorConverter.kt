package io.github.lumklar.sortrss.server.infrastructure.exception

import io.github.lumklar.sortrss.common.api.dto.ApiResultCode
import io.github.lumklar.sortrss.common.domain.model.enums.DomainErrorCode
import io.github.lumklar.sortrss.common.domain.model.enums.UserErrorCode

/**
 * 错误码转换器
 * 作用：隔离内部领域码与对外接口码
 * 原则：内部细粒度 → 对外通用化
 */
object ErrorConverter {

    /**
     * 领域业务错误码 → 对外标准响应码
     */
    fun DomainErrorCode.toResultCode(): ApiResultCode {
        return when (this) {
            is UserErrorCode -> when (this) {
//                UserErrorCode.USER_NOT_EXIST -> ApiResultCode.UNAUTHORIZED
                UserErrorCode.PASSWORD_ERROR -> ApiResultCode.UNAUTHORIZED
                else -> ApiResultCode.BUSINESS_ERROR;
            }
            // 其他所有业务错误
            else -> ApiResultCode.BUSINESS_ERROR
        }
    }

}