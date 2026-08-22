package io.github.lumklar.sortrss.common.domain.model.user

import io.github.lumklar.sortrss.common.domain.shared.exception.DomainException

/**
 * 用户不存在异常
 */
class UserNotFoundException(
    message: String = UserErrorCode.USER_NOT_EXIST.msg,
) : DomainException(
    domainCode = UserErrorCode.USER_NOT_EXIST,
    message = message
)

/**
 * 密码错误异常（一般用于登录认证）
 */
class PasswordErrorException(
    message: String = UserErrorCode.PASSWORD_ERROR.msg,
) : DomainException(
    domainCode = UserErrorCode.PASSWORD_ERROR,
    message = message
)

/**
 * 用户名为空异常
 */
class UsernameEmptyException(
    message: String = UserErrorCode.USERNAME_EMPTY.msg,
) : DomainException(
    domainCode = UserErrorCode.USERNAME_EMPTY,
    message = message
)

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
class OldPasswordMismatchException(
    message: String = UserErrorCode.OLD_PASSWORD_ERROR.msg,
) : DomainException(
    domainCode = UserErrorCode.OLD_PASSWORD_ERROR,
    message = message
)

/**
 * 用户名已存在异常
 */
class UsernameAlreadyExistsException(
    message: String = UserErrorCode.USERNAME_EXISTS.msg,
) : DomainException(
    domainCode = UserErrorCode.USERNAME_EXISTS,
    message = message
)

/**
 * 用户未设置密码
 */
class UserHasNoPasswordException(
    message: String = UserErrorCode.OLD_PASSWORD_MISS.msg,
) : DomainException(
    domainCode = UserErrorCode.OLD_PASSWORD_MISS,
    message = message
)

/**
 * 匿名用户不允许增加数据源
 */
class AnonymousUserDataSourceLimitException(
    message: String = UserErrorCode.ANONYMOUS_USER_DATA_SOURCE_LIMIT.msg,
) : DomainException(
    domainCode = UserErrorCode.ANONYMOUS_USER_DATA_SOURCE_LIMIT,
    message = message
)

/**
 * 非匿名用户不允许通过数据源登录
 */
class DataSourceAccessDeniedException(
    message: String = UserErrorCode.DATA_SOURCE_ACCESS_DENIED.msg,
) : DomainException(
    domainCode = UserErrorCode.DATA_SOURCE_ACCESS_DENIED,
    message = message
)
