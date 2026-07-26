package io.github.lumklar.sortrss.server.infrastructure.persistence.user.convert

import io.github.lumklar.sortrss.common.domain.model.user.User
import io.github.lumklar.sortrss.server.infrastructure.persistence.user.entity.UserPO
import kotlin.uuid.toJavaUuid
import kotlin.uuid.toKotlinUuid

/**
 * UserPO → User 领域模型
 */
fun UserPO.toDomain(): User {
    val id = this.id ?: throw IllegalArgumentException("Persisted UserPO must have id")
    val username = this.username
    val passwordHash = this.password

    require(username.isNotBlank()) { "Username cannot be blank" }
    require(passwordHash.isNotBlank()) { "Password hash cannot be blank" }

    val uuid = id.toKotlinUuid()
    return User.reconstruct(
        id = uuid,
        username = username,
        hash = passwordHash
    )
}

/**
 * User 领域模型 → UserPO
 */
fun User.toPO(): UserPO {
    return UserPO().apply {
        id = this@toPO.id.value.toJavaUuid()
        username = this@toPO.username.value
        password = this@toPO.getPasswordHash()
    }
}