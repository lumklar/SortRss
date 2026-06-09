package io.github.lumklar.sortrss.common.api.dto

/**
 * 全局统一返回结果
 * T：返回数据泛型
 * 密封类：强制处理成功/失败，类型安全
 */
sealed class ApiResult<out T> {

    /**
     * 成功响应
     */
    data class Success<out T>(
        val code: Int = ApiResultCode.SUCCESS.code,
        val msg: String = ApiResultCode.SUCCESS.msg,
        val data: T
    ) : ApiResult<T>()

    /**
     * 失败响应
     * @param code 对外标准错误码（前端用）
     * @param msg 对外提示信息
     * @param extra 扩展信息：领域错误 + 异常堆栈
     */
    data class Failure(
        val code: Int,
        val msg: String,
        val extra: ApiExtra? = null
    ) : ApiResult<Nothing>()

    /**
     * 伴生对象：简化创建（最常用写法）
     */
    companion object {
        /**
         *成功：仅数据
         */
        fun <T> success(data: T): Success<T> = Success(data = data)

        /**
         *成功：自定义提示
         */
        fun <T> success(data: T, msg: String): Success<T> = Success(msg = msg, data = data)

        /**
         *失败：标准对外码
         */
        fun failure(code: ApiResultCode): Failure = Failure(code.code, code.msg)

        /**
         * 标准失败：对外码 + 内部扩展信息（通用）
         */
        fun failure(code: ApiResultCode, extra: ApiExtra? = null): Failure {
            return Failure(code.code, code.msg, extra)
        }

        /**
         * 业务/框架错误失败：对外码 + 错误来源 + 内部错误码/信息
         */
        fun failure(
            code: ApiResultCode,
            errorSource: ErrorSource,
            errorCode: Int? = null,
            errorMsg: String? = null
        ): Failure {
            val extra = ApiExtra(
                errorSource = errorSource,
                errorCode = errorCode,
                errorMsg = errorMsg
            )
            return Failure(code.code, code.msg, extra)
        }

        /**
         * 完整失败：对外码 + 错误来源 + 内部错误 + 原生异常（全量信息）
         * 适用于：既需要业务错误码，又需要记录异常堆栈的场景
         */
        fun failure(
            code: ApiResultCode,
            errorSource: ErrorSource,
            errorCode: Int? = null,
            errorMsg: String? = null,
            stackTrace: List<String>? = null,
        ): Failure {
            val extra = ApiExtra(
                errorSource = errorSource,
                errorCode = errorCode,
                errorMsg = errorMsg,
                stackTrace = stackTrace
            )
            return Failure(code.code, code.msg, extra)
        }
    }
}
