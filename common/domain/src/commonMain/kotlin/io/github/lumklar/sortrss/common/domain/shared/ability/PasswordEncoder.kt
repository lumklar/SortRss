package io.github.lumklar.sortrss.common.domain.shared.ability

/**
 * 密码编码器接口，属于领域能力（Domain Ability）。
 * 具体实现由基础设施层提供（如 BCrypt）。
 */
interface PasswordEncoder {
    /** 对明文密码进行编码，返回密文 */
    fun encode(rawPassword: String): String

    /** 校验明文密码与密文是否匹配 */
    fun matches(rawPassword: String, encodedPassword: String): Boolean
}