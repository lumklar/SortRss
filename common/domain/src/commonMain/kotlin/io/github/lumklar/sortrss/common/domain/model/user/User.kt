package io.github.lumklar.sortrss.common.domain.model.user

import io.github.lumklar.sortrss.common.domain.shared.ability.PasswordEncoder

class User private constructor(
    val id: UserId,
    val username: Username,
    private var password: Password,
) {
    companion object {
        fun register(
            username: String,
            plainPassword: String,
            encoder: PasswordEncoder,
            policy: PasswordPolicy,
            id: UserId
        ): User {
            val uname = Username.fromString(username)
            val pwd = Password.encode(plainPassword, encoder, policy) // 内部若违反策略，会抛出 PasswordPolicyViolationException
            return User(id, uname, pwd)
        }

        fun reconstruct(id: Long, username: String, hash: String): User {
            val uname = Username.fromString(username)
            return User(UserId(id), uname, Password.fromHash(hash))
        }
    }

    fun verifyPassword(plain: String, encoder: PasswordEncoder): Boolean =
        password.matches(plain, encoder)

    fun changePassword(
        oldPlain: String,
        newPlain: String,
        encoder: PasswordEncoder,
        policy: PasswordPolicy
    ) {
        if (!verifyPassword(oldPlain, encoder)) {
            throw OldPasswordMismatchException()
        }
        this.password = Password.encode(newPlain, encoder, policy) // 内部可能抛出 PasswordPolicyViolationException
    }

    fun getPasswordHash(): String = password.asHash()
}