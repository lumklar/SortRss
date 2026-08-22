package io.github.lumklar.sortrss.common.domain.model.datasource

/**
 * 已验证的连接详情，只有通过远程验证后才能创建。
 */
class ValidatedConnectionDetails private constructor(
    val delegate: DataSourceConnectionDetails
) {
    companion object {
        /**
         * 创建已验证对象，只能由应用层在验证成功后调用。
         */
        internal fun of(connectionDetails: DataSourceConnectionDetails): ValidatedConnectionDetails {
            // 可在此进行额外检查，如是否远程类型等
            return ValidatedConnectionDetails(connectionDetails)
        }
    }

    // 方便领域服务获取原始信息
    val type get() = delegate.type
    val identityKey get() = delegate.identityKey
}
