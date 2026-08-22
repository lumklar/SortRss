package io.github.lumklar.sortrss.common.foundation.shared.ability.id

import io.github.lumklar.sortrss.common.domain.model.user.UserId
import io.github.lumklar.sortrss.common.domain.shared.ability.IdGenerator
import io.github.lumklar.sortrss.common.foundation.validation.password.UUIDGenerator

class UserIdGenerator : IdGenerator<UserId> {
    override fun next(): UserId {
        return UserId(UUIDGenerator.generateUuid())
    }
}
