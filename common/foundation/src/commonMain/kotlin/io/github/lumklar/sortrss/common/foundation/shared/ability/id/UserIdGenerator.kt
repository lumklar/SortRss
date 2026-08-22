package io.github.lumklar.sortrss.common.business.shared.ability.id

import io.github.lumklar.sortrss.common.domain.model.user.UserId
import io.github.lumklar.sortrss.common.domain.shared.ability.IdGenerator
import io.github.lumklar.sortrss.common.business.validation.password.UUIDGenerator

class UserIdGenerator : IdGenerator<UserId> {
    override fun next(): UserId {
        return UserId(UUIDGenerator.generateUuid())
    }
}
