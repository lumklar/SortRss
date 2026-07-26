package io.github.lumklar.sortrss.common.domain.model.user

import io.github.lumklar.sortrss.common.domain.shared.error.DomainErrorCode

enum class UserErrorCode(
    override val code: Int,
    override val msg: String
) : DomainErrorCode {
    USER_NOT_EXIST(2001, "用户不存在"),
    PASSWORD_ERROR(2002, "密码错误"),
    USERNAME_EMPTY(2003, "用户名不能为空"),
    PASSWORD_POLICY_VIOLATION(2004, "密码不符合安全策略"),
    OLD_PASSWORD_ERROR(2005, "原密码错误")
}