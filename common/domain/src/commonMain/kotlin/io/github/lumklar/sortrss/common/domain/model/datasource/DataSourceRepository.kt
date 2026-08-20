package io.github.lumklar.sortrss.common.domain.model.datasource

import io.github.lumklar.sortrss.common.domain.model.user.UserId

interface DataSourceRepository {
    fun save(dataSource: DataSource): DataSource
    fun findById(id: DataSourceId): DataSource?
    fun findByUserId(userId: UserId): List<DataSource>
    fun findByConnectionDetails(details: DataSourceConnectionDetails): DataSource?
    fun existsByConnectionDetails(details: DataSourceConnectionDetails): Boolean
    fun delete(dataSource: DataSource)
}