package io.github.lumklar.sortrss.server.infrastructure.config

import io.github.lumklar.sortrss.common.api.dto.ApiExtra
import io.github.lumklar.sortrss.common.api.dto.ApiResult
import io.github.lumklar.sortrss.common.api.dto.ApiResultCode
import io.github.lumklar.sortrss.common.api.dto.ErrorSource
import io.github.lumklar.sortrss.common.domain.model.exception.BusinessException
import io.github.lumklar.sortrss.common.shared.utils.ExceptionStackTraceUtil
import org.springframework.dao.DataAccessException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.resource.NoResourceFoundException


/**
 * Spring Boot 4 全局异常处理器
 * 统一封装返回 ApiResult，根据配置控制是否暴露 ApiExtra
 */
@RestControllerAdvice
class GlobalExceptionHandler(
    private val apiResponseProperties: ApiResponseProperties
) {

    // ====================== 1. 自定义业务异常（核心） ======================
    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(e: BusinessException): ApiResult<Nothing> {
        val domainCode = e.domainCode
        // 根据配置决定是否构建 ApiExtra
        val extra = buildExtra(
            source = ErrorSource.DOMAIN,
            errorCode = domainCode.code,
            errorMsg = domainCode.msg,
            throwable = e
        )
        return ApiResult.failure(ApiResultCode.BUSINESS_ERROR, extra)
    }

    // ====================== 2. 请求参数异常 ======================
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidException(e: MethodArgumentNotValidException): ApiResult<Nothing> {
        // 拼接参数错误信息
        val errors = e.bindingResult.fieldErrors.joinToString(";") {
            "${it.field}: ${it.defaultMessage}"
        }
        val extra = buildExtra(source = ErrorSource.WEB, errorMsg = errors)
        return ApiResult.failure(ApiResultCode.PARAM_ERROR, extra)
    }

    // 缺失参数异常
    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleMissingParamException(e: MissingServletRequestParameterException): ApiResult<Nothing> {
        val msg = "缺失参数：${e.parameterName}"
        val extra = buildExtra(source = ErrorSource.WEB, errorMsg = msg)
        return ApiResult.failure(ApiResultCode.PARAM_ERROR, extra)
    }

    // ====================== 3. 权限/资源异常 ======================
    // 无权限
    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDeniedException(e: AccessDeniedException): ApiResult<Nothing> {
        val extra = buildExtra(source = ErrorSource.WEB, throwable = e)
        return ApiResult.failure(ApiResultCode.NOT_FOUND, extra)
    }

    // 404 资源不存在
    @ExceptionHandler(NoResourceFoundException::class)
    fun handleNotFoundException(e: NoResourceFoundException): ApiResult<Nothing> {
        val extra = buildExtra(source = ErrorSource.WEB, throwable = e)
        return ApiResult.failure(ApiResultCode.NOT_FOUND, extra)
    }

    // ====================== 4. 数据库异常 ======================
    @ExceptionHandler(DataAccessException::class)
    fun handleDbException(e: DataAccessException): ApiResult<Nothing> {
        val extra = buildExtra(source = ErrorSource.SYSTEM, throwable = e)
        return ApiResult.failure(ApiResultCode.DB_ERROR, extra)
    }

    // ====================== 5. 系统兜底异常 ======================
    @ExceptionHandler(Exception::class)
    fun handleGlobalException(e: Exception): ApiResult<Nothing> {
        val extra = buildExtra(source = ErrorSource.SYSTEM, throwable = e)
        return ApiResult.failure(ApiResultCode.SERVER_ERROR, extra)
    }

    // ====================== 工具方法：根据配置构建 ApiExtra ======================
    /**
     * 核心：配置控制是否返回 ApiExtra
     */
    private fun buildExtra(
        source: ErrorSource,
        errorCode: Int? = null,
        errorMsg: String? = null,
        throwable: Throwable? = null
    ): ApiExtra? {
        // 配置开启隐藏 → 直接返回 null
        if (apiResponseProperties.excludeExtra) {
            return null
        }
        // 未开启隐藏 → 构建完整扩展信息
        return ApiExtra(
            errorSource = source,
            errorCode = errorCode,
            errorMsg = errorMsg,
            stackTrace = ExceptionStackTraceUtil.formatStackTrace(throwable)
        )
    }
}
