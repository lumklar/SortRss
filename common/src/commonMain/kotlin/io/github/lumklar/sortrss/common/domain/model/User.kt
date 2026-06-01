package io.github.lumklar.sortrss.common.domain.model

import io.github.lumklar.sortrss.common.domain.service.PasswordEncoder

class User private constructor(
    val id: Long,
    val username: String,
    private var passwordHash: String,
    private val passwordEncoder: PasswordEncoder
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
            return User(id, username.trim(), hash, passwordEncoder)
        }

        fun fromPersistence(
            id: Long,
            username: String,
            passwordHash: String,
            passwordEncoder: PasswordEncoder
        ): User {
            require(username.isNotBlank()) { "用户名不能为空" }
            return User(id, username.trim(), passwordHash, passwordEncoder)
        }
    }

    fun verifyPassword(plainPassword: String): Boolean =
        passwordEncoder.matches(plainPassword, passwordHash)

    fun changePassword(oldPlain: String, newPlain: String) {
        require(verifyPassword(oldPlain)) { "原密码错误" }
        require(newPlain.length >= MIN_PASSWORD_LENGTH) {
            "新密码长度不能小于 $MIN_PASSWORD_LENGTH"
        }
        passwordHash = passwordEncoder.encode(newPlain)
    }

    fun getPasswordHash(): String = passwordHash
}
