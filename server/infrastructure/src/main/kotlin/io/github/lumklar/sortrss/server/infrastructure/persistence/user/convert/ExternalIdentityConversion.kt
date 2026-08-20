package io.github.lumklar.sortrss.server.infrastructure.persistence.user.convert

import io.github.lumklar.sortrss.common.domain.model.user.ExternalIdentity
import io.github.lumklar.sortrss.server.infrastructure.persistence.user.entity.UserExternalIdentityPO
import kotlin.uuid.toJavaUuid
import kotlin.uuid.toKotlinUuid

fun ExternalIdentity.toPersistence( ): UserExternalIdentityPO {
    return UserExternalIdentityPO(
        id = this.id.value.toJavaUuid(),
        userId = this.userId.value.toJavaUuid(),
        provider = this.provider,
        subject = this.subject,
    )
}

fun UserExternalIdentityPO.toDomain(): ExternalIdentity {
    return ExternalIdentity.reconstruct(
        id = this.id!!.toKotlinUuid(),
        userId = this.userId!!.toKotlinUuid(),
        provider = this.provider!!,
        subject = this.subject!!,
    )
}
