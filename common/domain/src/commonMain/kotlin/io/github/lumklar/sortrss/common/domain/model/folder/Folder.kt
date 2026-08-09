package io.github.lumklar.sortrss.common.domain.model.folder

import io.github.lumklar.sortrss.common.domain.model.datasource.DataSourceId
import io.github.lumklar.sortrss.common.domain.shared.enums.DataSourceType

class Folder private constructor(
    val id: FolderId,
    val dataSourceId: DataSourceId,
    initialName: FolderName,
    private val parentFolderId: FolderId?,
    // 冗余字段
    val dataSourceType: DataSourceType,
    // 业务字段：该文件夹在数据源中的分组ID
    val sourceGroupId: String? = null
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
            parentFolderId: FolderId? = null,
            sourceGroupId: String? = null   // 新增业务参数
        ): Folder {
            return Folder(
                id,
                dataSourceId,
                FolderName.from(name),
                parentFolderId,
                dataSourceType,
                sourceGroupId
            )
        }

        fun reconstruct(
            id: FolderId,
            dataSourceId: DataSourceId,
            name: String,
            parentFolderId: FolderId?,
            dataSourceType: DataSourceType,
            sourceGroupId: String? = null   // 新增业务参数
        ): Folder {
            return Folder(
                id,
                dataSourceId,
                FolderName.from(name),
                parentFolderId,
                dataSourceType,
                sourceGroupId
            )
        }
    }

    fun rename(newName: String) {
        name = FolderName.from(newName)
    }
}