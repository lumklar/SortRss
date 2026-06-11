package io.github.lumklar.sortrss.common.domain.service
import io.github.lumklar.sortrss.common.domain.model.ability.PasswordEncoder
import io.github.lumklar.sortrss.common.domain.model.entity.User

/**
 * 业务领域服务：处理用户核心业务逻辑
 * 注入 领域能力（PasswordEncoder）
 */
class UserDomainService(
    private val passwordEncoder: PasswordEncoder
) {
    /**
     * 注册用户（封装实体创建 + 加密逻辑）
     */
    fun registerUser(username: String, plainPassword: String): User {
        return User.register(
            username = username,
            plainPassword = plainPassword,
            passwordEncoder = passwordEncoder
        )
    }

    /**
     * 修改密码（封装实体方法）
     */
    fun changeUserPassword(user: User, oldPlain: String, newPlain: String) {
        user.changePassword(oldPlain, newPlain, passwordEncoder)
    }
}

