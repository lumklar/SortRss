package io.github.lumklar.sortrss.server.infrastructure.persistence.convert

import io.github.lumklar.sortrss.common.domain.model.user.User
import io.github.lumklar.sortrss.server.infrastructure.persistence.entity.UserPO
import org.springframework.stereotype.Component

@Component
class UserConverter {

    fun toDomain(po: UserPO): User {
        val id = po.id
        val username = po.username
        val passwordHash = po.password

        requireNotNull(id) { "Persisted UserPO must have id" }
        require(username.isNotBlank()) { "Username cannot be blank" }
        require(passwordHash.isNotBlank()) { "Password hash cannot be blank" }

        return User.reconstruct(
            id = id,
            username = username,
            hash = passwordHash
        )
    }

    fun toPO(user: User): UserPO {
        return UserPO().apply {
            // 假设 UserId 有 value: Long 属性，0 表示未持久化
            id = if (user.id.value == 0L) null else user.id.value
            username = user.username
            password = user.getPasswordHash()
        }
    }
}