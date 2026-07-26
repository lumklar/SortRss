package io.github.lumklar.sortrss.common.domain.model.datasource

import io.github.lumklar.sortrss.common.domain.model.user.UserId

interface DataSourceRepository {
    fun save(dataSource: DataSource): DataSource
    fun findById(id: DataSourceId): DataSource?
    fun findByUserId(userId: UserId): List<DataSource>
    fun delete(dataSource: DataSource)
}