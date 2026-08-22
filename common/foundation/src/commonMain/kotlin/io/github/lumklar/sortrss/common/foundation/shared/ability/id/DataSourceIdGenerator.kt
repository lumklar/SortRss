package io.github.lumklar.sortrss.common.foundation.shared.ability.id

import io.github.lumklar.sortrss.common.domain.model.datasource.DataSourceId
import io.github.lumklar.sortrss.common.domain.shared.ability.IdGenerator
import io.github.lumklar.sortrss.common.foundation.validation.password.UUIDGenerator

class DataSourceIdGenerator : IdGenerator<DataSourceId> {
    override fun next(): DataSourceId {
        return DataSourceId(UUIDGenerator.generateUuid())
    }
}