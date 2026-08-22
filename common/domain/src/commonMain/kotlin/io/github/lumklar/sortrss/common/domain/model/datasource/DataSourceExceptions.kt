package io.github.lumklar.sortrss.common.domain.model.datasource

import io.github.lumklar.sortrss.common.domain.shared.exception.DomainException

/** 数据源不存在 */
class DataSourceNotFoundException(
    message: String = DataSourceErrorCode.DATASOURCE_NOT_FOUND.msg,
) : DomainException(
    domainCode = DataSourceErrorCode.DATASOURCE_NOT_FOUND,
    message = message
)

/** 数据源已存在（例如重复添加） */
class DataSourceAlreadyExistsException(
    message: String = DataSourceErrorCode.DATASOURCE_ALREADY_EXISTS.msg,
) : DomainException(
    domainCode = DataSourceErrorCode.DATASOURCE_ALREADY_EXISTS,
    message = message
)

/** 不支持的数据源类型 */
class UnsupportedDataSourceTypeException(
    message: String = DataSourceErrorCode.DATASOURCE_TYPE_UNSUPPORTED.msg,
) : DomainException(
    domainCode = DataSourceErrorCode.DATASOURCE_TYPE_UNSUPPORTED,
    message = message
)

/** 订阅源已关联到此数据源 */
class SubscriptionAlreadyExistsException(
    message: String = DataSourceErrorCode.SUBSCRIPTION_ALREADY_EXISTS.msg,
) : DomainException(
    domainCode = DataSourceErrorCode.SUBSCRIPTION_ALREADY_EXISTS,
    message = message
)

/** 订阅关联不存在 */
class SubscriptionNotFoundException(
    message: String = DataSourceErrorCode.SUBSCRIPTION_NOT_FOUND.msg,
) : DomainException(
    domainCode = DataSourceErrorCode.SUBSCRIPTION_NOT_FOUND,
    message = message
)

/** 订阅连接失败 */
class DataSourceConnectionException(
    message: String = DataSourceErrorCode.DATA_SOURCE_CONNECTION_EXCEPTION.msg,
    cause: Throwable? = null,
) : DomainException(
    domainCode = DataSourceErrorCode.DATA_SOURCE_CONNECTION_EXCEPTION,
    message = message,
    cause = cause,
)
