package io.github.lumklar.sortrss.server.infrastructure.persistence.folder.convert

import io.github.lumklar.sortrss.common.domain.model.datasource.DataSourceId
import io.github.lumklar.sortrss.common.domain.model.feed.FeedId
import io.github.lumklar.sortrss.common.domain.model.folder.Folder
import io.github.lumklar.sortrss.common.domain.model.folder.FolderId
import io.github.lumklar.sortrss.common.domain.model.folder.FolderMembership
import io.github.lumklar.sortrss.common.domain.model.folder.FolderMembershipId
import io.github.lumklar.sortrss.server.infrastructure.persistence.folder.entity.FolderMembershipPO
import io.github.lumklar.sortrss.server.infrastructure.persistence.folder.entity.FolderPO
import kotlin.uuid.toJavaUuid
import kotlin.uuid.toKotlinUuid

fun Folder.toPO(): FolderPO {
    return FolderPO(
        id = this.id.value.toJavaUuid(),
        dataSourceId = this.dataSourceId.value.toJavaUuid(),
        name = this.name.value,
        parentFolderId = this.parentId?.value?.toJavaUuid(),
        dataSourceType = this.dataSourceType,
        sourceGroupId = this.sourceGroupId,   // 映射新字段
        gmtCreate = null,
        gmtModify = null
    )
}

fun FolderPO.toDomain(): Folder {
    val id = requireNotNull(this.id) { "FolderPO.id must not be null" }
    val dataSourceId = requireNotNull(this.dataSourceId) { "FolderPO.dataSourceId must not be null" }
    val name = requireNotNull(this.name) { "FolderPO.name must not be null" }
    val dataSourceType = requireNotNull(this.dataSourceType) { "FolderPO.dataSourceType must not be null" }

    return Folder.reconstruct(
        id = FolderId(id.toKotlinUuid()),
        dataSourceId = DataSourceId(dataSourceId.toKotlinUuid()),
        name = name,
        parentFolderId = this.parentFolderId?.toKotlinUuid()?.let { FolderId(it) },
        dataSourceType = dataSourceType,
        sourceGroupId = this.sourceGroupId    // 映射新字段
    )
}

// FolderMembership 的转换保持不变（未涉及新字段）
fun FolderMembership.toPO(): FolderMembershipPO {
    return FolderMembershipPO(
        id = this.id.value.toJavaUuid(),
        folderId = this.folderId.value.toJavaUuid(),
        feedId = this.feedId.value.toJavaUuid(),
        dataSourceType = this.dataSourceType,
        gmtCreate = null,
        gmtModify = null
    )
}

fun FolderMembershipPO.toDomain(): FolderMembership {
    val id = requireNotNull(this.id) { "FolderMembershipPO.id must not be null" }
    val folderId = requireNotNull(this.folderId) { "FolderMembershipPO.folderId must not be null" }
    val feedId = requireNotNull(this.feedId) { "FolderMembershipPO.feedId must not be null" }
    val dataSourceType = requireNotNull(this.dataSourceType) { "FolderMembershipPO.dataSourceType must not be null" }

    return FolderMembership.reconstruct(
        id = FolderMembershipId(id.toKotlinUuid()),
        folderId = FolderId(folderId.toKotlinUuid()),
        feedId = FeedId(feedId.toKotlinUuid()),
        dataSourceType = dataSourceType
    )
}