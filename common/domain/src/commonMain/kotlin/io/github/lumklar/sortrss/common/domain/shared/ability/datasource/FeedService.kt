package io.github.lumklar.sortrss.common.domain.shared.ability.datasource


interface FeedService {
    suspend fun getFeeds(): List<Feed>
    suspend fun getGroups(): List<Group>
    suspend fun getItems(
        sinceId: Long? = null,
        maxId: Long? = null,
        withIds: List<String>? = null  // 逗号分隔转 List
    ): List<Item>

    suspend fun getUnreadItemIds(): List<String>
    suspend fun getSavedItemIds(): List<String>
    suspend fun getFavicons(): List<Favicon>?

    suspend fun markItemAsRead(itemId: String)
    suspend fun saveItem(itemId: String)
    suspend fun unsaveItem(itemId: String)

    suspend fun markFeedAsRead(feedId: String, before: Long)   // before 必填
    suspend fun markGroupAsRead(groupId: String, before: Long) // before 必填

    suspend fun unreadRecentlyRead()  // 取消最近阅读的未读状态

    // Hot Links
    suspend fun getLinks(
        offset: Int? = null,
        range: Int? = null,
        page: Int? = null
    ): List<Link>
}

