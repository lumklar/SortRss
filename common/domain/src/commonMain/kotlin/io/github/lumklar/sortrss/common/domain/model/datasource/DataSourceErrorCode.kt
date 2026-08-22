package io.github.lumklar.sortrss.common.domain.model.datasource

import io.github.lumklar.sortrss.common.domain.shared.error.DomainErrorCode

enum class DataSourceErrorCode(
    override val code: Int,
    override val msg: String
) : DomainErrorCode {
    DATASOURCE_NOT_FOUND(3001, "数据源不存在"),
    DATASOURCE_ALREADY_EXISTS(3002, "数据源已存在"),
    DATASOURCE_TYPE_UNSUPPORTED(3003, "不支持的数据源类型"),
    SUBSCRIPTION_ALREADY_EXISTS(3004, "该订阅源已在此数据源中"),
    SUBSCRIPTION_NOT_FOUND(3005, "订阅关联不存在"),
    DATA_SOURCE_CONNECTION_EXCEPTION(3006,"订阅源连接异常")
}