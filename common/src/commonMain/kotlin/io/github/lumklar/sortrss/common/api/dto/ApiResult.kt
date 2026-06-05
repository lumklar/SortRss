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
//     * @param subCode 内部领域错误码（排查问题用，不强制前端处理）
     */
    data class Failure(
        val code: Int,
        val msg: String,
//        val subCode: Int? = null
    ) : ApiResult<Nothing>()

    /**
     * 伴生对象：简化创建（最常用写法）
     */
    companion object {
        // 成功：仅数据
        fun <T> success(data: T): Success<T> = Success(data = data)

        // 成功：自定义提示
        fun <T> success(data: T, msg: String): Success<T> = Success(msg = msg, data = data)

        // 失败：标准对外码
        fun failure(code: ApiResultCode): Failure = Failure(code.code, code.msg)

        // 失败：标准码 + 内部子码
//        fun failure(code: ApiResultCode, subCode: Int): Failure = Failure(code.code, code.msg, subCode)

        // 失败：标准码 + 自定义信息 + 内部子码
//        fun failure(code: ApiResultCode, msg: String, subCode: Int? = null): Failure = Failure(code.code, msg, subCode)
    }
}

