package io.github.lumklar.sortrss.common.domain.model.ability

/**
 * 密码编码器接口（领域层抽象）
 * 具体实现由基础设施层提供（如 BCrypt、Argon2）
 */
interface PasswordEncoder {
    fun encode(rawPassword: String): String
    fun matches(rawPassword: String, encodedPassword: String): Boolean
}