package io.github.lumklar.sortrss.common.domain.model.datasource

import io.github.lumklar.sortrss.common.domain.shared.enums.DataSourceType

/**
 * 数据源连接详情（领域层抽象）。
 * 领域层不关心具体协议细节，只要求能提供类型和业务身份标识。
 */
interface DataSourceConnectionDetails {
    val type: DataSourceType

    /**
     * 业务身份标识（用于判断两个连接是否指向同一外部数据源）。
     * - 远程数据源：返回稳定且唯一的字符串（如 "fever:endpoint:username"）。
     * - 本地数据源：无外部标识，返回 null。
     */
    val identityKey: String

    /**
     * 判断两个连接详情是否代表同一数据源。
     * 默认实现基于 identityKey 比较，子类可按需覆盖。
     */
    fun hasSameIdentityAs(other: DataSourceConnectionDetails): Boolean {
        val thisKey = identityKey
        val otherKey = other.identityKey
        return thisKey == otherKey
    }

    /**
     * 判断两个连接详情是否完全一致（包括所有属性，如密码）。
     * 默认实现基于 equals，子类如有特殊需求可覆盖。
     */
    fun hasSameContentAs(other: DataSourceConnectionDetails): Boolean {
        return this == other
    }
}