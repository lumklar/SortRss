package io.github.lumklar.sortrss.common.infrastructure.shared.ability.api.fever

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class FeverFeed(
    val id: Int?,
    @SerialName("title") val title: String?,
    @SerialName("favicon_id") val faviconId: Int?,
    @SerialName("url") val url: String?,
    @SerialName("site_url") val siteUrl: String?,
    @SerialName("is_spark") val isSpark: Int?,
    @SerialName("last_updated_on_time") val lastUpdatedOnTime: Long?
)

@Serializable
internal data class FeverGroup(
    val id: Int?,
    val title: String?
)

@Serializable
internal data class FeverFavicon(
    val id: Int?,
    val data: String?  // base64 encoded, prefixed with image type
)

@Serializable
internal data class FeverItem(
    val id: Int?,
    @SerialName("feed_id") val feedId: Int?,
    val title: String,
    val author: String?,
    val html: String?,
    val url: String?,
    @SerialName("is_saved") val isSaved: Int,
    @SerialName("is_read") val isRead: Int,
    @SerialName("created_on_time") val createdOnTime: Long
)

@Serializable
internal data class FeverFeedsGroup(
    @SerialName("group_id") val groupId: Int,
    @SerialName("feed_ids") val feedIds: String  // comma-separated list
)

@Serializable
internal data class FeverLink(
    val id: Int,
    @SerialName("feed_id") val feedId: Int? = null,
    @SerialName("item_id") val itemId: Int? = null,
    val temperature: Float,
    @SerialName("is_item") val isItem: Int,
    @SerialName("is_local") val isLocal: Int,
    @SerialName("is_saved") val isSaved: Int? = null,
    val title: String,
    val url: String,
    @SerialName("item_ids") val itemIds: String  // comma-separated list
)

// ---------- 响应包装 ----------
@Serializable
internal data class FeverFeedsResponse(
    val feeds: List<FeverFeed>,
    @SerialName("feeds_groups") val feedsGroups: List<FeverFeedsGroup>? = null,
    val auth: Int,
    @SerialName("api_version") val apiVersion: Int,
    @SerialName("last_refreshed_on_time") val lastRefreshedOnTime: Long
)

@Serializable
internal data class FeverGroupsResponse(
    val groups: List<FeverGroup>,
    @SerialName("feeds_groups") val feedsGroups: List<FeverFeedsGroup>? = null,
    val auth: Int,
    @SerialName("api_version") val apiVersion: Int,
    @SerialName("last_refreshed_on_time") val lastRefreshedOnTime: Long
)

@Serializable
internal data class FeverFaviconsResponse(
    val favicons: List<FeverFavicon>,   // 文档字段名为 favicons
    val auth: Int,
    @SerialName("api_version") val apiVersion: Int,
    @SerialName("last_refreshed_on_time") val lastRefreshedOnTime: Long
)

@Serializable
internal data class FeverItemsResponse(
    val items: List<FeverItem>,
    @SerialName("total_items") val totalItems: Int,
    val auth: Int,
    @SerialName("api_version") val apiVersion: Int,
    @SerialName("last_refreshed_on_time") val lastRefreshedOnTime: Long
)

@Serializable
internal data class FeverUnreadIdsResponse(
    @SerialName("unread_item_ids") val unreadItemIds: String?,
    val auth: Int,
    @SerialName("api_version") val apiVersion: Int,
    @SerialName("last_refreshed_on_time") val lastRefreshedOnTime: Long
)

@Serializable
internal data class FeverSavedIdsResponse(
    @SerialName("saved_item_ids") val savedItemIds: String?,
    val auth: Int,
    @SerialName("api_version") val apiVersion: Int,
    @SerialName("last_refreshed_on_time") val lastRefreshedOnTime: Long
)

@Serializable
internal data class FeverLinksResponse(
    val links: List<FeverLink>,
    val auth: Int,
    @SerialName("api_version") val apiVersion: Int,
    @SerialName("last_refreshed_on_time") val lastRefreshedOnTime: Long
)

// 基础认证响应（仅用于验证）
@Serializable
internal data class FeverAuthResponse(val auth: Int)
