package io.github.lumklar.sortrss.common.domain.model.folder

import io.github.lumklar.sortrss.common.domain.model.datasource.DataSourceId

interface FolderRepository {
    fun save(folder: Folder): Folder
    fun findById(id: FolderId): Folder?
    fun findByDataSourceId(dataSourceId: DataSourceId): List<Folder>
    fun delete(id: FolderId)
}
