package io.github.lumklar.sortrss.common.domain.model.datasource

import io.github.lumklar.sortrss.common.domain.shared.exception.DomainException

/** 数据源不存在 */
class DataSourceNotFoundException : DomainException(DataSourceErrorCode.DATASOURCE_NOT_FOUND)

/** 数据源已存在（例如重复添加） */
class DataSourceAlreadyExistsException : DomainException(DataSourceErrorCode.DATASOURCE_ALREADY_EXISTS)

/** 不支持的数据源类型 */
class UnsupportedDataSourceTypeException : DomainException(DataSourceErrorCode.DATASOURCE_TYPE_UNSUPPORTED)

/** 订阅源已关联到此数据源 */
class SubscriptionAlreadyExistsException : DomainException(DataSourceErrorCode.SUBSCRIPTION_ALREADY_EXISTS)

/** 订阅关联不存在 */
class SubscriptionNotFoundException : DomainException(DataSourceErrorCode.SUBSCRIPTION_NOT_FOUND)