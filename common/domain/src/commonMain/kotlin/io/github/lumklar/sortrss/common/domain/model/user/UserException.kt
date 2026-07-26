package io.github.lumklar.sortrss.common.domain.model.user

import io.github.lumklar.sortrss.common.domain.shared.exception.DomainException

/**
 * 用户不存在异常
 */
class UserNotFoundException : DomainException(UserErrorCode.USER_NOT_EXIST)

/**
 * 密码错误异常（一般用于登录认证）
 */
class PasswordErrorException : DomainException(UserErrorCode.PASSWORD_ERROR)

/**
 * 用户名为空异常
 */
class UsernameEmptyException : DomainException(UserErrorCode.USERNAME_EMPTY)

/**
 * 密码不符合安全策略异常（由 PasswordPolicy 校验抛出）
 */
class PasswordPolicyViolationException(
    message: String = UserErrorCode.PASSWORD_POLICY_VIOLATION.msg,
) : DomainException(
    domainCode = UserErrorCode.PASSWORD_POLICY_VIOLATION,
    message = message
)

/**
 * 原密码错误异常（用于修改密码时验证旧密码）
 */
class OldPasswordMismatchException : DomainException(UserErrorCode.OLD_PASSWORD_ERROR)