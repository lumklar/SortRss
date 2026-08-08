package io.github.lumklar.sortrss.common.infrastructure.shared.ability.api.fever

import io.github.lumklar.sortrss.common.domain.shared.ability.datasource.Feed
import io.github.lumklar.sortrss.common.domain.shared.ability.datasource.Group
import io.github.lumklar.sortrss.common.domain.shared.ability.datasource.Item
import io.github.lumklar.sortrss.common.domain.shared.ability.datasource.Link

internal object FeverMapper {

    // groupId -> feedId 列表
    fun buildGroupFeedMap(feedsGroups: List<FeverFeedsGroup>?): Map<String, List<String>> {
        return feedsGroups?.associate { fg ->
            fg.groupId.toString() to fg.feedIds.split(",")
                .map { it.trim() }
                .filter { it.isNotBlank() }
        } ?: emptyMap()
    }

    // feedId -> groupId 列表 (一个 feed 可属于多个组)
    fun buildFeedGroupMap(feedsGroups: List<FeverFeedsGroup>?): Map<String, List<String>> {
        val map = mutableMapOf<String, MutableList<String>>()
        feedsGroups?.forEach { fg ->
            val groupId = fg.groupId.toString()
            fg.feedIds.split(",")
                .map { it.trim() }
                .filter { it.isNotBlank() }
                .forEach { feedId ->
                    map.getOrPut(feedId) { mutableListOf() }.add(groupId)
                }
        }
        return map
    }

    fun toFeed(feverFeed: FeverFeed, feedGroupMap: Map<String, List<String>>): Feed {
        val feedId = feverFeed.id?.toString() ?: ""
        return Feed(
            id = feedId,
            name = feverFeed.title,
            siteUrl = feverFeed.siteUrl,
            feedUrl = feverFeed.url,
            lastUpdated = feverFeed.lastUpdatedOnTime,
            iconUrl = null,  // favicon 由调用方自行填充
            isSpark = feverFeed.isSpark == 1,
            groupIds = feedGroupMap[feedId] ?: emptyList()
        )
    }

    fun toGroup(feverGroup: FeverGroup, groupFeedMap: Map<String, List<String>>): Group {
        val groupId = feverGroup.id?.toString() ?: ""
        return Group(
            id = groupId,
            title = feverGroup.title ?: "",
            feedIds = groupFeedMap[groupId] ?: emptyList()
        )
    }

    fun toItem(feverItem: FeverItem): Item {
        return Item(
            id = feverItem.id?.toString() ?: "",
            feedId = feverItem.feedId?.toString() ?: "",
            title = feverItem.title,
            author = feverItem.author,
            content = feverItem.html,
            link = feverItem.url,
            isRead = feverItem.isRead == 1,
            isStarred = feverItem.isSaved == 1,
            createdAt = feverItem.createdOnTime
        )
    }

    fun toLink(feverLink: FeverLink): Link {
        return Link(
            id = feverLink.id.toString(),
            feedId = feverLink.feedId?.toString(),
            itemId = feverLink.itemId?.toString(),
            temperature = feverLink.temperature,
            isItem = feverLink.isItem == 1,
            isLocal = feverLink.isLocal == 1,
            isSaved = feverLink.isSaved == 1,
            title = feverLink.title,
            url = feverLink.url,
            itemIds = feverLink.itemIds.split(",")
                .map { it.trim() }
                .filter { it.isNotBlank() }
        )
    }
}