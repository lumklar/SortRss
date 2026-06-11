package io.github.lumklar.sortrss.common.domain.model.enums

enum class UserErrorCode(
    override val code: Int,
    override val msg: String
) : DomainErrorCode {
    USER_NOT_EXIST(2001, "用户不存在"),
    PASSWORD_ERROR(2002, "密码错误")
}
