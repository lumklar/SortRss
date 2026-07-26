package io.github.lumklar.sortrss.server.infrastructure.persistence.datasource.convert

import io.github.lumklar.sortrss.common.domain.model.datasource.DataSource
import io.github.lumklar.sortrss.common.domain.model.datasource.DataSourceId
import io.github.lumklar.sortrss.common.domain.model.datasource.DataSourceType
import io.github.lumklar.sortrss.common.domain.model.user.UserId
import io.github.lumklar.sortrss.server.infrastructure.persistence.datasource.entity.DataSourcePo
import io.github.lumklar.sortrss.server.infrastructure.util.toJavaInstantOrNull
import io.github.lumklar.sortrss.server.infrastructure.util.toKotlinInstantOrNull
import kotlin.uuid.toJavaUuid
import kotlin.uuid.toKotlinUuid

fun DataSource.toPo(): DataSourcePo = DataSourcePo().apply {
    // 领域 id (Uuid) -> java.util.UUID
    id = this@toPo.id.value.toJavaUuid()
    userId = this@toPo.userId.value.toJavaUuid()
    type = this@toPo.type.ordinal
    name = this@toPo.name.value
    connectionDetails = DataSourceConnectionDetailsConverter().convertToDatabaseColumn(this@toPo.connectionDetails)
    lastSyncTime = this@toPo.lastSyncTime.toJavaInstantOrNull()
    lastAllReadAt = this@toPo.lastAllReadAt.toJavaInstantOrNull()
}

fun DataSourcePo.toDomain(): DataSource {
    val connectionDetails = this.connectionDetails
        ?.let { DataSourceConnectionDetailsConverter().convertToEntityAttribute(it) }
        ?: error("connectionDetails missing in database")

    return DataSource.reconstruct(
        id = DataSourceId(this.id!!.toKotlinUuid()),
        userId = UserId(this.userId!!.toKotlinUuid()),
        type = DataSourceType.values()[this.type!!],
        name = this.name!!,
        connectionDetails = connectionDetails,
        lastSyncTime = this.lastSyncTime.toKotlinInstantOrNull(),
        lastAllReadAt = this.lastAllReadAt.toKotlinInstantOrNull()
    )
}