package io.github.lumklar.sortrss.server.application.assembler

import io.github.lumklar.sortrss.common.domain.model.user.User
import io.github.lumklar.sortrss.server.application.pojo.command.UserDto

/**
 * 将 User 聚合根转换为 UserDto（应用层扩展）
 */
internal fun User.toDto(): UserDto {
    return UserDto(
        id = this.id.value.toString(),
        username = this.username.value   // 假设 username 是值对象，内部持有 String
    )
}
