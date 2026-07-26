package io.github.lumklar.sortrss.common.domain.model.user

import io.github.lumklar.sortrss.common.domain.shared.ability.PasswordEncoder

internal class Password private constructor(private val hash: String) {
    companion object {
        internal fun encode(plain: String, encoder: PasswordEncoder, policy: PasswordPolicy): Password {
            policy.validate(plain) // 规则校验内聚在此
            return Password(encoder.encode(plain))
        }

        internal fun fromHash(hash: String): Password {
            return Password(hash)
        }
    }

    // 核心：验证逻辑，不对外暴露 hash 字符串
    internal fun matches(plain: String, encoder: PasswordEncoder): Boolean =
        encoder.matches(plain, hash)

    // 只用于持久化映射（JPA/MyBatis 可访问），业务层不可见
    internal fun asHash(): String = hash
}
