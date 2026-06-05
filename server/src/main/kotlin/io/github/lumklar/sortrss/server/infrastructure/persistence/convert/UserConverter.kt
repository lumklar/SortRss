package io.github.lumklar.sortrss.server.infrastructure.persistence.convert

import io.github.lumklar.sortrss.common.domain.model.entity.User
import io.github.lumklar.sortrss.server.infrastructure.persistence.entity.UserPO
import org.springframework.stereotype.Component

@Component
class UserConverter(
) {

    fun toDomain(po: UserPO): User {
        val id = po.id          // 局部变量，不可变
        val username = po.username
        val passwordHash = po.password

        requireNotNull(id) { "Persisted UserPO must have id" }
        require(username.isNotBlank()) { "Username cannot be blank" }
        require(passwordHash.isNotBlank()) { "Password hash cannot be blank" }

        return User.fromPersistence(
            id = id,
            username = username,
            passwordHash = passwordHash,
        )
    }

    fun toPO(user: User): UserPO {
        return UserPO().apply {
            id = if (user.id == 0L) null else user.id
            username = user.username
            password = user.getPasswordHash()
        }
    }
}