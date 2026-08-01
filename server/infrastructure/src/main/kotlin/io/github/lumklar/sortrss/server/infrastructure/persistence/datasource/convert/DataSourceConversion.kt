package io.github.lumklar.sortrss.server.infrastructure.persistence.datasource.convert

import io.github.lumklar.sortrss.common.domain.model.datasource.DataSource
import io.github.lumklar.sortrss.common.domain.model.datasource.DataSourceId
import io.github.lumklar.sortrss.common.domain.model.user.UserId
import io.github.lumklar.sortrss.server.infrastructure.persistence.datasource.entity.DataSourcePo
import io.github.lumklar.sortrss.server.infrastructure.util.toJavaInstantOrNull
import io.github.lumklar.sortrss.server.infrastructure.util.toKotlinInstantOrNull
import kotlin.uuid.toJavaUuid
import kotlin.uuid.toKotlinUuid

fun DataSource.toPO(): DataSourcePo {
    return DataSourcePo(
        id = this.id.value.toJavaUuid(),
        userId = this.userId.value.toJavaUuid(),
        type = this.type,
        name = this.name.value,
        connectionDetails = DataSourceConnectionDetailsConverter().convertToDatabaseColumn(this.connectionDetails),
        lastSyncTime = this.lastSyncTime.toJavaInstantOrNull(),
        lastAllReadAt = this.lastAllReadAt.toJavaInstantOrNull(),
        gmtCreate = null,
        gmtModify = null
    )
}

fun DataSourcePo.toDomain(): DataSource {
    val id = requireNotNull(this.id) { "DataSourcePo.id must not be null" }
    val userId = requireNotNull(this.userId) { "DataSourcePo.userId must not be null" }
    val type = requireNotNull(this.type) { "DataSourcePo.type must not be null" }
    val name = requireNotNull(this.name) { "DataSourcePo.name must not be null" }
    val connectionDetails = this.connectionDetails
        ?.let { DataSourceConnectionDetailsConverter().convertToEntityAttribute(it) }
        ?: error("connectionDetails missing in database")

    return DataSource.reconstruct(
        id = DataSourceId(id.toKotlinUuid()),
        userId = UserId(userId.toKotlinUuid()),
        type = type,
        name = name,
        connectionDetails = connectionDetails,
        lastSyncTime = this.lastSyncTime.toKotlinInstantOrNull(),
        lastAllReadAt = this.lastAllReadAt.toKotlinInstantOrNull()
    )
}

