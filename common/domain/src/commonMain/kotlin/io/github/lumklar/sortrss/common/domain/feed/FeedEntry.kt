package io.github.lumklar.sortrss.common.domain.model.entity

import io.github.lumklar.sortrss.common.domain.shared.valueobject.EntryId
import io.github.lumklar.sortrss.common.domain.shared.valueobject.FeedId
import kotlin.time.Clock
import kotlin.time.Instant

/**
 * Feed 与 Entry 的多对多关联实体。
 * 仅用于表示某篇文章出现在某个 Feed 中，无额外业务行为。
 */
class FeedEntry(
    val feedId: FeedId,
    val entryId: EntryId,
    val createdAt: Instant = Clock.System.now()
) {
}