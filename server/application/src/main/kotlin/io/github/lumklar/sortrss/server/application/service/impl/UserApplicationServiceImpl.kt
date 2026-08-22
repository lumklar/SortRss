package io.github.lumklar.sortrss.server.application.service.impl

import io.github.lumklar.sortrss.common.domain.model.user.*
import io.github.lumklar.sortrss.common.domain.shared.ability.IdGenerator
import io.github.lumklar.sortrss.common.domain.shared.ability.PasswordEncoder
import io.github.lumklar.sortrss.server.application.assembler.toDto
import io.github.lumklar.sortrss.server.application.pojo.user.command.RegisterUserCommand
import io.github.lumklar.sortrss.server.application.pojo.user.dto.UserDto
import io.github.lumklar.sortrss.server.application.pojo.user.query.UserQuery
import io.github.lumklar.sortrss.server.application.service.UserApplicationService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserApplicationServiceImpl(
    private val userIdGenerator: IdGenerator<UserId>,
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val passwordPolicy: PasswordPolicy
) : UserApplicationService {

    /**
     * 注册新用户（命令模式）。
     *
     * @param command 包含用户名和明文密码的命令对象
     * @return 注册成功的用户 DTO
     * @throws UsernameAlreadyExistsException 如果用户名已被占用
     */
    @Transactional
    override fun register(command: RegisterUserCommand): UserDto {
        // 1. 校验用户名唯一性
        if (userRepository.existsByUsername(Username.fromBusinessString(command.username))) {
            throw UsernameAlreadyExistsException("Username '${command.username}' already exists")
        }

        // 2. 生成用户ID
        val userId = userIdGenerator.next()

        // 3. 调用领域工厂创建用户
        val user = User.register(
            rawUsername = command.username,
            plainPassword = command.plainPassword,
            encoder = passwordEncoder,
            policy = passwordPolicy,
            id = userId
        )

        // 4. 持久化
        val savedUser = userRepository.save(user)

        // 5. 转换为 DTO 返回
        return savedUser.toDto()
    }

    @Transactional(readOnly = true)
    override fun queryUser(query: UserQuery): UserDto {
        // 当前仅使用 username 查询，校验非空
        val username = query.username ?: throw UsernameEmptyException("Username must not be null")

        val user = userRepository.findByUsername(Username.fromBusinessString(username))
            ?: throw UserNotFoundException("User with username '$username' not found")

        return user.toDto()
    }
}