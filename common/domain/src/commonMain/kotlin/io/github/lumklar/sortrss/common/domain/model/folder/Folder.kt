package io.github.lumklar.sortrss.common.domain.model.folder

import io.github.lumklar.sortrss.common.domain.model.datasource.DataSourceId
import io.github.lumklar.sortrss.common.domain.shared.enums.DataSourceType

class Folder private constructor(
    val id: FolderId,
    val dataSourceId: DataSourceId,
    initialName: FolderName,
    private val parentFolderId: FolderId?,
    //冗余字段
    val dataSourceType: DataSourceType
) {
    var name: FolderName = initialName
        private set

    val displayName: String get() = name.value
    val parentId: FolderId? get() = parentFolderId

    companion object {
        fun create(
            id: FolderId,
            dataSourceId: DataSourceId,
            name: String,
            dataSourceType: DataSourceType,
            parentFolderId: FolderId? = null
        ): Folder {
            return Folder(id, dataSourceId, FolderName.from(name), parentFolderId, dataSourceType)
        }

        fun reconstruct(
            id: FolderId,
            dataSourceId: DataSourceId,
            name: String,
            parentFolderId: FolderId?,
            dataSourceType: DataSourceType
        ): Folder {
            return Folder(id, dataSourceId, FolderName.from(name), parentFolderId, dataSourceType)
        }
    }

    fun rename(newName: String) {
        name = FolderName.from(newName)
    }
}