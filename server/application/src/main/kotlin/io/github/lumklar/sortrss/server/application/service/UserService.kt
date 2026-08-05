package io.github.lumklar.sortrss.server.application.service

import io.github.lumklar.sortrss.server.application.pojo.user.command.RegisterUserCommand
import io.github.lumklar.sortrss.server.application.pojo.user.dto.UserDto
import io.github.lumklar.sortrss.server.application.pojo.user.query.UserQuery
import org.springframework.stereotype.Service

/**
 * 用户应用服务，提供用户注册等用例。
 */
@Service
interface UserService {
    fun register(command: RegisterUserCommand): UserDto
    /**
     * 根据查询条件查询用户信息。
     * 当前只按 username 查询，若 username 为 null 则抛出异常（或你可以自定义为空时的行为）。
     * userId 暂未使用，但已预留。
     *
     * @param query 包含 username（必填）和可选 userId 的查询对象
     * @return 用户 DTO
     * @throws IllegalArgumentException 如果 username 为空
     * @throws UserNotFoundException 如果用户不存在
     */
    fun queryUser(query: UserQuery): UserDto
}
