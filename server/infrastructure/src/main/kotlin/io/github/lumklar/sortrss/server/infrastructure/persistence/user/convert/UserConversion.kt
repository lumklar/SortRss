package io.github.lumklar.sortrss.server.infrastructure.persistence.user.convert

import io.github.lumklar.sortrss.common.domain.model.user.User
import io.github.lumklar.sortrss.server.infrastructure.persistence.user.entity.UserPO
import kotlin.uuid.toJavaUuid
import kotlin.uuid.toKotlinUuid

/**
 * UserPO → User 领域模型
 */
fun UserPO.toDomain(): User {
    // 所有必需字段必须存在，否则视为数据损坏
    val id = requireNotNull(this.id) { "UserPO.id must not be null" }
    val username = requireNotNull(this.username) { "UserPO.username must not be null" }
    val password = requireNotNull(this.password) { "UserPO.password must not be null" }

    require(username.isNotBlank()) { "Username cannot be blank" }
    require(password.isNotBlank()) { "Password hash cannot be blank" }

    val uuid = id.toKotlinUuid()
    return User.reconstruct(
        id = uuid,
        username = username,
        hash = password
    )
}

/**
 * User 领域模型 → UserPO
 */
fun User.toPO(): UserPO {
    return UserPO(
        id = this.id.value.toJavaUuid(),
        username = this.username.value,
        password = this.getPasswordHash(),
        gmtCreate = null,
        gmtModify = null
    )
}