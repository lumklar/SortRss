package io.github.lumklar.sortrss.common.domain.model.folder

import io.github.lumklar.sortrss.common.domain.model.feed.FeedId

/**
 * 文件夹与订阅源的关联对象。
 * 记录某个 Feed 被组织到哪个 Folder 中。
 */
data class FolderFeed(
    val folderId: FolderId,
    val feedId: FeedId
)