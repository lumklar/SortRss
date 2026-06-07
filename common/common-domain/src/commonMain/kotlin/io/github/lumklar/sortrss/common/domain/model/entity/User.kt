package io.github.lumklar.sortrss.common.domain.model.entity

import io.github.lumklar.sortrss.common.domain.model.ability.PasswordEncoder

// io.github.lumklar.sortrss.common.domain.model.entity.User
class User private constructor(
    val id: Long,
    val username: String,
    private var passwordHash: String,
) {

    companion object {
        const val MIN_PASSWORD_LENGTH = 8

        fun register(
            username: String,
            plainPassword: String,
            passwordEncoder: PasswordEncoder,
            id: Long = 0L
        ): User {
            require(username.isNotBlank()) { "用户名不能为空" }
            require(plainPassword.length >= MIN_PASSWORD_LENGTH) {
                "密码长度不能小于 $MIN_PASSWORD_LENGTH"
            }
            val hash = passwordEncoder.encode(plainPassword)
            return User(id, username.trim(), hash)
        }

        fun fromPersistence(
            id: Long,
            username: String,
            passwordHash: String,
        ): User {
            require(username.isNotBlank()) { "用户名不能为空" }
            return User(id, username.trim(), passwordHash)
        }
    }

    // ==================== 修复：加密器作为参数传入 ====================
    fun verifyPassword(plainPassword: String, passwordEncoder: PasswordEncoder): Boolean =
        passwordEncoder.matches(plainPassword, passwordHash)

    fun changePassword(
        oldPlain: String,
        newPlain: String,
        passwordEncoder: PasswordEncoder
    ) {
        require(verifyPassword(oldPlain, passwordEncoder)) { "原密码错误" }
        require(newPlain.length >= MIN_PASSWORD_LENGTH) {
            "新密码长度不能小于 $MIN_PASSWORD_LENGTH"
        }
        passwordHash = passwordEncoder.encode(newPlain)
    }

    fun getPasswordHash(): String = passwordHash
}

