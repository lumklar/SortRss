package io.github.lumklar.sortrss.common.foundation.shared.ability.id

import io.github.lumklar.sortrss.common.domain.model.folder.FolderMembershipId
import io.github.lumklar.sortrss.common.domain.shared.ability.IdGenerator
import io.github.lumklar.sortrss.common.foundation.validation.password.UUIDGenerator

class FolderMembershipIdGenerator: IdGenerator<FolderMembershipId> {
    override fun next(): FolderMembershipId {
        return FolderMembershipId(UUIDGenerator.generateUuid());
    }
}