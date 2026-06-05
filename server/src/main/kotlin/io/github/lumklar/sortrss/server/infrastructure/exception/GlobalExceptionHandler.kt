package io.github.lumklar.sortrss.server.infrastructure.exception

import io.github.lumklar.sortrss.common.api.dto.ApiResult
import io.github.lumklar.sortrss.common.domain.model.exception.BusinessException
import io.github.lumklar.sortrss.server.infrastructure.exception.ErrorConverter.toResultCode
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(e: BusinessException): ApiResult<Nothing> {
        val resultCode = e.domainCode.toResultCode()

        return ApiResult.failure(resultCode);
    }
}
