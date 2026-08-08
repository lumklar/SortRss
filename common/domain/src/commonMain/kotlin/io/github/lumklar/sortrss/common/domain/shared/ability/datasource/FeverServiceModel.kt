package io.github.lumklar.sortrss.common.domain.shared.ability.datasource

data class Feed(
    val id: String,
    val name: String?,
    val siteUrl: String?,
    val feedUrl: String?,
    val lastUpdated: Long?,
    val iconUrl: String?,
    val isSpark: Boolean = false,
    val groupIds: List<String> = emptyList()  // 一个 Feed 可属于多个 Group
)

data class Group(
    val id: String,
    val title: String,
    val feedIds: List<String> = emptyList()
)

data class Item(
    val id: String,
    val feedId: String,
    val title: String,
    val author: String?,
    val content: String?,
    val link: String?,
    val isRead: Boolean,
    val isStarred: Boolean,
    val createdAt: Long
)

data class Link(
    val id: String,
    val feedId: String?,        // 仅当 isItem == true 时有效
    val itemId: String?,        // 仅当 isItem == true 时有效
    val temperature: Float,
    val isItem: Boolean,
    val isLocal: Boolean,
    val isSaved: Boolean,       // 仅当 isItem == true 时有效
    val title: String,
    val url: String,
    val itemIds: List<String>   // 关联的 Item ID 列表
)

data class Favicon(
    val id: String,
    val data: String  // base64 编码的图像数据，可直接用于 data: 协议
)