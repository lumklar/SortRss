package io.github.lumklar.sortrss.common.foundation.shared.ability.id

import io.github.lumklar.sortrss.common.domain.model.user.ExternalIdentityId
import io.github.lumklar.sortrss.common.domain.shared.ability.IdGenerator
import io.github.lumklar.sortrss.common.foundation.validation.password.UUIDGenerator

class ExternalIdentityIdGenerator : IdGenerator<ExternalIdentityId> {
    override fun next(): ExternalIdentityId {
        return ExternalIdentityId(UUIDGenerator.generateUuid())
    }
}
