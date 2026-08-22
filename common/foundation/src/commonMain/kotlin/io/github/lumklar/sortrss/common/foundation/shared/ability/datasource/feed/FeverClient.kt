package io.github.lumklar.sortrss.common.foundation.shared.ability.datasource.fever

import io.github.lumklar.sortrss.common.domain.model.datasource.DataSourceConnectionException
import io.github.lumklar.sortrss.common.domain.shared.ability.datasource.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.io.IOException
import kotlinx.serialization.json.Json
import org.kotlincrypto.hash.md.MD5

class FeverClient(
    private val baseUrl: String,
    private val username: String,
    private val password: String,
    private val httpClient: HttpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            }, ContentType.parse("text/json"))
        }
    }
) : FeedService {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val apiKey: String by lazy {
        val raw = "$username:$password"
        val md5 = MD5()
        val hashBytes = md5.digest(raw.encodeToByteArray())
        hashBytes.joinToString("") { byte ->
            (byte.toInt() and 0xFF).toString(16).padStart(2, '0')
        }
    }

    // ------------------- 通用请求 -------------------
    /**
     * 读取请求：action 和参数放在 URL 查询字符串中（如 ?api&feeds&since_id=10），
     * api_key 仍在 POST body 中（API 文档要求）。
     */
    private suspend inline fun <reified T> getRequest(
        action: String,
        params: Map<String, String> = emptyMap()
    ): T {
        val url = URLBuilder().apply {
            takeFrom(baseUrl)
            // 添加无值参数 api 和 action
            parameters.append("api", "")
            if (action.isNotEmpty()) {
                parameters.append(action, "")
            }
            // 添加其他查询参数
            params.forEach { (k, v) -> parameters.append(k, v) }
        }.buildString()

        val response: HttpResponse = httpClient.post(url) {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(FormDataContent(Parameters.build {
                append("api_key", apiKey)
            }))
        }

        if (response.status != HttpStatusCode.OK) {
            throw IOException("请求失败: ${response.status}")
        }

        val bodyString = response.body<String>()
        val authResponse = json.decodeFromString<FeverAuthResponse>(bodyString)
        if (authResponse.auth != 1) throw IOException("Fever 认证失败")
        return json.decodeFromString(bodyString)
    }

    /**
     * 写入请求：所有参数（包括 api_key）均放在 POST body 中
     */
    private suspend inline fun <reified T> writeRequest(
        params: Map<String, String>
    ): T {
        val url = baseUrl + "?api"
        val response: HttpResponse = httpClient.post(url) {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(FormDataContent(Parameters.build {
                append("api_key", apiKey)
                params.forEach { (k, v) -> append(k, v) }
            }))
        }

        if (response.status != HttpStatusCode.OK) {
            throw IOException("请求失败: ${response.status}")
        }

        val bodyString = response.body<String>()
        val authResponse = json.decodeFromString<FeverAuthResponse>(bodyString)
        if (authResponse.auth != 1) throw IOException("Fever 认证失败")
        return json.decodeFromString(bodyString)
    }

    // 无需返回值的写入
    private suspend fun writeRequestNoResult(params: Map<String, String>) {
        writeRequest<Map<String, String>>(params) // 忽略返回值
    }

    // ------------------- 实现接口 -------------------
    override suspend fun validateConnection() {
        // Fever API 的认证测试端点：GET 或 POST ?api
        // 使用现有的 getRequest，传入空 action，会构造 URL: ?api
        // getRequest 内部已经检查 auth == 1，失败会抛出 IOException
        try {
            getRequest<FeverAuthResponse>("")
        } catch (e: IOException) {
            throw DataSourceConnectionException("Fever 连接验证失败", e)
        } catch (e: Exception) {
            throw DataSourceConnectionException("Fever 连接验证异常", e)
        }
    }

    override suspend fun getFeeds(): List<Feed> {
        val dto = getRequest<FeverFeedsResponse>("feeds")
        val feedGroupMap = FeverMapper.buildFeedGroupMap(dto.feedsGroups)
        return dto.feeds.map { FeverMapper.toFeed(it, feedGroupMap) }
    }

    override suspend fun getGroups(): List<Group> {
        val dto = getRequest<FeverGroupsResponse>("groups")
        val groupFeedMap = FeverMapper.buildGroupFeedMap(dto.feedsGroups)
        return dto.groups.map { FeverMapper.toGroup(it, groupFeedMap) }
    }

    override suspend fun getItems(
        sinceId: Long?,
        maxId: Long?,
        withIds: List<String>?
    ): List<Item> {
        val params = mutableMapOf<String, String>()
        sinceId?.let { params["since_id"] = it.toString() }
        maxId?.let { params["max_id"] = it.toString() }
        withIds?.let { params["with_ids"] = it.joinToString(",") }
        val dto = getRequest<FeverItemsResponse>("items", params)
        return dto.items.map { FeverMapper.toItem(it) }
    }

    override suspend fun getUnreadItemIds(): List<String> {
        val dto = getRequest<FeverUnreadIdsResponse>("unread_item_ids")
        return dto.unreadItemIds
            ?.split(",")
            ?.map { it.trim() }
            ?.filter { it.isNotBlank() }
            ?: emptyList()
    }

    override suspend fun getSavedItemIds(): List<String> {
        val dto = getRequest<FeverSavedIdsResponse>("saved_item_ids")
        return dto.savedItemIds
            ?.split(",")
            ?.map { it.trim() }
            ?.filter { it.isNotBlank() }
            ?: emptyList()
    }

    override suspend fun getFavicons(): List<Favicon>? {
        val dto = getRequest<FeverFaviconsResponse>("favicons")
        return dto.favicons
            .map { Favicon(id = it.id?.toString() ?: "", data = it.data ?: "") }
            .ifEmpty { null }
    }

    override suspend fun markItemAsRead(itemId: String) {
        writeRequestNoResult(
            mapOf(
                "mark" to "item",
                "as" to "read",
                "id" to itemId
            )
        )
    }

    override suspend fun saveItem(itemId: String) {
        writeRequestNoResult(
            mapOf(
                "mark" to "item",
                "as" to "saved",
                "id" to itemId
            )
        )
    }

    override suspend fun unsaveItem(itemId: String) {
        writeRequestNoResult(
            mapOf(
                "mark" to "item",
                "as" to "unsaved",
                "id" to itemId
            )
        )
    }

    override suspend fun markFeedAsRead(feedId: String, before: Long) {
        writeRequestNoResult(
            mapOf(
                "mark" to "feed",
                "as" to "read",
                "id" to feedId,
                "before" to before.toString()
            )
        )
    }

    override suspend fun markGroupAsRead(groupId: String, before: Long) {
        writeRequestNoResult(
            mapOf(
                "mark" to "group",
                "as" to "read",
                "id" to groupId,
                "before" to before.toString()
            )
        )
    }

    override suspend fun unreadRecentlyRead() {
        writeRequestNoResult(mapOf("unread_recently_read" to "1"))
    }

    override suspend fun getLinks(
        offset: Int?,
        range: Int?,
        page: Int?
    ): List<Link> {
        val params = mutableMapOf<String, String>()
        offset?.let { params["offset"] = it.toString() }
        range?.let { params["range"] = it.toString() }
        page?.let { params["page"] = it.toString() }
        val dto = getRequest<FeverLinksResponse>("links", params)
        return dto.links.map { FeverMapper.toLink(it) }
    }
}