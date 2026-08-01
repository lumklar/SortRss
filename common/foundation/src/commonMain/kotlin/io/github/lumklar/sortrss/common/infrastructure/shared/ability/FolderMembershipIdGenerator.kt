package io.github.lumklar.sortrss.common.infrastructure.shared.ability

import io.github.lumklar.sortrss.common.domain.model.folder.FolderMembershipId
import io.github.lumklar.sortrss.common.domain.shared.ability.IdGenerator
import io.github.lumklar.sortrss.common.infrastructure.validation.password.UUIDGenerator

class FolderMembershipIdGenerator: IdGenerator<FolderMembershipId> {
    override fun next(): FolderMembershipId {
        return FolderMembershipId(UUIDGenerator.generateUuid());
    }
}