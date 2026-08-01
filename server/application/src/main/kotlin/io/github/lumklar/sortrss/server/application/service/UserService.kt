package io.github.lumklar.sortrss.server.application.service

import io.github.lumklar.sortrss.server.application.pojo.command.RegisterUserCommand
import io.github.lumklar.sortrss.server.application.pojo.command.UserDto
import org.springframework.stereotype.Service

/**
 * 用户应用服务，提供用户注册等用例。
 */
@Service
interface UserService {
    fun register(command: RegisterUserCommand): UserDto
}
