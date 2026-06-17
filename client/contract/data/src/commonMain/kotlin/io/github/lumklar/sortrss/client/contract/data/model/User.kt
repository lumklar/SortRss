package io.github.lumklar.sortrss.client.contract.data.model


class User private constructor(
    val id: Long,
    val username: String,
    val passwordHash: String,
) {
    companion object {
        const val MIN_PASSWORD_LENGTH = 8

        fun register(
            username: String,
            plainPassword: String,
            id: Long = 0
        ): User {
            require(username.isNotBlank()) { "用户名不能为空" }
            require(plainPassword.length >= MIN_PASSWORD_LENGTH) {
                "密码长度不能小于 $MIN_PASSWORD_LENGTH"
            }
            return User(id, username.trim(), "asdkjghk")
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
}
