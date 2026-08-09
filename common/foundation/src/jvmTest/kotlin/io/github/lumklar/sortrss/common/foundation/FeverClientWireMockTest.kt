package io.github.lumklar.sortrss.common.foundation

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.options
import io.github.lumklar.sortrss.common.foundation.shared.ability.api.fever.FeverClient
import io.ktor.client.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class FeverClientWireMockTest {

    private lateinit var wireMockServer: WireMockServer
    private lateinit var client: FeverClient

    // 固定测试凭证（api_key 将基于此计算）
    private val username = "testuser"
    private val password = "testpass"
    private val baseUrl: String get() = "http://localhost:${wireMockServer.port()}"

    // 模拟数据常量
    private val now = System.currentTimeMillis() / 1000
    private val feedId1 = 1
    private val feedId2 = 2
    private val groupId1 = 10
    private val groupId2 = 20
    private val itemId1 = 100
    private val itemId2 = 101
    private val itemId3 = 102

    @BeforeEach
    fun setup() {
        wireMockServer = WireMockServer(options().dynamicPort())
        wireMockServer.start()
        configureFor("localhost", wireMockServer.port())

        // 初始化客户端（使用默认 HttpClient，无需额外配置）
        client = FeverClient(baseUrl, username, password, HttpClient())

        // 配置所有 Stub
        stubFeeds()
        stubGroups()
        stubItems()
        stubUnreadIds()
        stubSavedIds()
        stubFavicons()
        stubLinks()
        stubMarkItemRead()
        stubSaveItem()
        stubUnsaveItem()
        stubMarkFeedRead()
        stubMarkGroupRead()
        stubUnreadRecentlyRead()
        stubItemsWithIds()  // 处理 with_ids 参数
        stubItemsPagination()  // 处理 since_id / max_id
    }

    @AfterEach
    fun tearDown() {
        wireMockServer.stop()
    }

    // ------------------- Stub 定义 -------------------

    private fun stubFeeds() {
        stubFor(
            post(urlPathEqualTo("/"))
                .withQueryParam("api", equalTo(""))
                .withQueryParam("feeds", equalTo(""))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/json")
                        .withBody(
                            """
                            {
                                "feeds": [
                                    {"id": $feedId1, "title": "Feed 1", "favicon_id": 1001, "url": "https://feed1.xml", "site_url": "https://feed1.com", "is_spark": 0, "last_updated_on_time": $now},
                                    {"id": $feedId2, "title": "Feed 2", "favicon_id": 1002, "url": "https://feed2.xml", "site_url": "https://feed2.com", "is_spark": 1, "last_updated_on_time": $now}
                                ],
                                "feeds_groups": [
                                    {"group_id": $groupId1, "feed_ids": "$feedId1"},
                                    {"group_id": $groupId2, "feed_ids": "$feedId2"}
                                ],
                                "auth": 1,
                                "api_version": 3,
                                "last_refreshed_on_time": $now
                            }
                            """.trimIndent()
                        )
                )
        )
    }

    private fun stubGroups() {
        stubFor(
            post(urlPathEqualTo("/"))
                .withQueryParam("api", equalTo(""))
                .withQueryParam("groups", equalTo(""))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/json")
                        .withBody(
                            """
                            {
                                "groups": [
                                    {"id": $groupId1, "title": "Group 1"},
                                    {"id": $groupId2, "title": "Group 2"}
                                ],
                                "feeds_groups": [
                                    {"group_id": $groupId1, "feed_ids": "$feedId1"},
                                    {"group_id": $groupId2, "feed_ids": "$feedId2"}
                                ],
                                "auth": 1,
                                "api_version": 3,
                                "last_refreshed_on_time": $now
                            }
                            """.trimIndent()
                        )
                )
        )
    }

    private fun stubItems() {
        // 默认 /items（无额外参数）返回所有 item
        stubFor(
            post(urlPathEqualTo("/"))
                .withQueryParam("api", equalTo(""))
                .withQueryParam("items", equalTo(""))
                .withQueryParam("since_id", absent())  // 没有 since_id
                .withQueryParam("max_id", absent())
                .withQueryParam("with_ids", absent())
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/json")
                        .withBody(buildItemsResponse(listOf(itemId1, itemId2, itemId3)))
                )
        )
    }

    private fun stubItemsPagination() {
        // since_id
        stubFor(
            post(urlPathEqualTo("/"))
                .withQueryParam("api", equalTo(""))
                .withQueryParam("items", equalTo(""))
                .withQueryParam("since_id", matching("\\d+"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/json")
                        .withBody(buildItemsResponse(listOf(itemId2, itemId3)))
                )
        )
        // max_id
        stubFor(
            post(urlPathEqualTo("/"))
                .withQueryParam("api", equalTo(""))
                .withQueryParam("items", equalTo(""))
                .withQueryParam("max_id", matching("\\d+"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/json")
                        .withBody(buildItemsResponse(listOf(itemId1, itemId2)))
                )
        )
    }

    private fun stubItemsWithIds() {
        // 匹配 with_ids=100（精确）
        stubFor(
            post(urlPathEqualTo("/"))
                .withQueryParam("api", equalTo(""))
                .withQueryParam("items", equalTo(""))
                .withQueryParam("with_ids", equalTo("100"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/json")
                        .withBody(
                            """
                        {
                            "items": [
                                {"id": $itemId1, "feed_id": $feedId1, "title": "Item 1", "author": "Author 1", "html": "<p>Content 1</p>", "url": "https://item1.com", "is_saved": 0, "is_read": 0, "created_on_time": ${now - 100}}
                            ],
                            "total_items": 1,
                            "auth": 1,
                            "api_version": 3,
                            "last_refreshed_on_time": $now
                        }
                        """.trimIndent()
                        )
                )
        )

        // 匹配 with_ids=101（精确）
        stubFor(
            post(urlPathEqualTo("/"))
                .withQueryParam("api", equalTo(""))
                .withQueryParam("items", equalTo(""))
                .withQueryParam("with_ids", equalTo("101"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/json")
                        .withBody(
                            """
                        {
                            "items": [
                                {"id": $itemId2, "feed_id": $feedId1, "title": "Item 2", "author": "Author 2", "html": "<p>Content 2</p>", "url": "https://item2.com", "is_saved": 0, "is_read": 0, "created_on_time": ${now - 50}}
                            ],
                            "total_items": 1,
                            "auth": 1,
                            "api_version": 3,
                            "last_refreshed_on_time": $now
                        }
                        """.trimIndent()
                        )
                )
        )

        // 匹配 with_ids=100,101（精确，用于 testGetItemsWithIds）
        stubFor(
            post(urlPathEqualTo("/"))
                .withQueryParam("api", equalTo(""))
                .withQueryParam("items", equalTo(""))
                .withQueryParam("with_ids", equalTo("100,101"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/json")
                        .withBody(
                            """
                        {
                            "items": [
                                {"id": $itemId1, "feed_id": $feedId1, "title": "Item 1", "author": "Author 1", "html": "<p>Content 1</p>", "url": "https://item1.com", "is_saved": 0, "is_read": 0, "created_on_time": ${now - 100}},
                                {"id": $itemId2, "feed_id": $feedId1, "title": "Item 2", "author": "Author 2", "html": "<p>Content 2</p>", "url": "https://item2.com", "is_saved": 0, "is_read": 0, "created_on_time": ${now - 50}}
                            ],
                            "total_items": 2,
                            "auth": 1,
                            "api_version": 3,
                            "last_refreshed_on_time": $now
                        }
                        """.trimIndent()
                        )
                )
        )
    }

    private fun stubUnreadIds() {
        stubFor(
            post(urlPathEqualTo("/"))
                .withQueryParam("api", equalTo(""))
                .withQueryParam("unread_item_ids", equalTo(""))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/json")
                        .withBody(
                            """
                            {
                                "unread_item_ids": "$itemId1,$itemId3",
                                "auth": 1,
                                "api_version": 3,
                                "last_refreshed_on_time": $now
                            }
                            """.trimIndent()
                        )
                )
        )
    }

    private fun stubSavedIds() {
        stubFor(
            post(urlPathEqualTo("/"))
                .withQueryParam("api", equalTo(""))
                .withQueryParam("saved_item_ids", equalTo(""))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/json")
                        .withBody(
                            """
                            {
                                "saved_item_ids": "$itemId2",
                                "auth": 1,
                                "api_version": 3,
                                "last_refreshed_on_time": $now
                            }
                            """.trimIndent()
                        )
                )
        )
    }

    private fun stubFavicons() {
        stubFor(
            post(urlPathEqualTo("/"))
                .withQueryParam("api", equalTo(""))
                .withQueryParam("favicons", equalTo(""))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/json")
                        .withBody(
                            """
                            {
                                "favicons": [
                                    {"id": 1001, "data": "iVBORw0KGgoAAAANSUhEUgAA...（模拟base64）"},
                                    {"id": 1002, "data": "iVBORw0KGgoAAAANSUhEUgAA...（模拟base64）"}
                                ],
                                "auth": 1,
                                "api_version": 3,
                                "last_refreshed_on_time": $now
                            }
                            """.trimIndent()
                        )
                )
        )
    }

    private fun stubLinks() {
        // 默认 links（无 offset/range/page）
        stubFor(
            post(urlPathEqualTo("/"))
                .withQueryParam("api", equalTo(""))
                .withQueryParam("links", equalTo(""))
                .withQueryParam("page", absent())
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/json")
                        .withBody(
                            """
                            {
                                "links": [
                                    {"id": 1001, "feed_id": $feedId1, "item_id": $itemId1, "temperature": 3.5, "is_item": 1, "is_local": 1, "is_saved": 0, "title": "Hot Link 1", "url": "https://link1.com", "item_ids": "$itemId1"},
                                    {"id": 1002, "feed_id": null, "item_id": null, "temperature": 2.0, "is_item": 0, "is_local": 0, "is_saved": null, "title": "Hot Link 2", "url": "https://link2.com", "item_ids": "$itemId2,$itemId3"}
                                ],
                                "auth": 1,
                                "api_version": 3,
                                "last_refreshed_on_time": $now
                            }
                            """.trimIndent()
                        )
                )
        )
        // page=2
        stubFor(
            post(urlPathEqualTo("/"))
                .withQueryParam("api", equalTo(""))
                .withQueryParam("links", equalTo(""))
                .withQueryParam("page", equalTo("2"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/json")
                        .withBody(
                            """
                            {
                                "links": [
                                    {"id": 2001, "feed_id": $feedId2, "item_id": $itemId3, "temperature": 4.0, "is_item": 1, "is_local": 1, "is_saved": 1, "title": "Hot Link 3", "url": "https://link3.com", "item_ids": "$itemId3"}
                                ],
                                "auth": 1,
                                "api_version": 3,
                                "last_refreshed_on_time": $now
                            }
                            """.trimIndent()
                        )
                )
        )
    }

    // ---------- 写入操作 Stub ----------
    private fun stubMarkItemRead() {
        stubFor(
            post(urlPathEqualTo("/"))
                .withQueryParam("api", equalTo(""))
                .withRequestBody(containing("mark=item"))
                .withRequestBody(containing("as=read"))
                .willReturn(authSuccess())
        )
    }

    private fun stubSaveItem() {
        stubFor(
            post(urlPathEqualTo("/"))
                .withQueryParam("api", equalTo(""))
                .withRequestBody(containing("mark=item"))
                .withRequestBody(containing("as=saved"))
                .willReturn(authSuccess())
        )
    }

    private fun stubUnsaveItem() {
        stubFor(
            post(urlPathEqualTo("/"))
                .withQueryParam("api", equalTo(""))
                .withRequestBody(containing("mark=item"))
                .withRequestBody(containing("as=unsaved"))
                .willReturn(authSuccess())
        )
    }

    private fun stubMarkFeedRead() {
        stubFor(
            post(urlPathEqualTo("/"))
                .withQueryParam("api", equalTo(""))
                .withRequestBody(containing("mark=feed"))
                .withRequestBody(containing("as=read"))
                .willReturn(authSuccess())
        )
    }

    private fun stubMarkGroupRead() {
        stubFor(
            post(urlPathEqualTo("/"))
                .withQueryParam("api", equalTo(""))
                .withRequestBody(containing("mark=group"))
                .withRequestBody(containing("as=read"))
                .willReturn(authSuccess())
        )
    }

    private fun stubUnreadRecentlyRead() {
        stubFor(
            post(urlPathEqualTo("/"))
                .withQueryParam("api", equalTo(""))
                .withRequestBody(containing("unread_recently_read=1"))
                .willReturn(authSuccess())
        )
    }

    private fun authSuccess() = aResponse()
        .withStatus(200)
        .withHeader("Content-Type", "text/json")
        .withBody("""{"auth": 1}""")

    // ---------- 辅助方法 ----------
    private fun buildItemsResponse(ids: List<Int>): String {
        val items = ids.map { id ->
            // 所有 Item 初始均未读、未保存
            val isRead = 0
            val isSaved = 0
            val title = "Item $id"
            """
        {"id": $id, "feed_id": $feedId1, "title": "$title", "author": "Author $id", "html": "<p>Content $id</p>", "url": "https://item$id.com", "is_saved": $isSaved, "is_read": $isRead, "created_on_time": ${now - id.toLong()}}
        """.trimIndent()
        }.joinToString(",")
        return """
    {
        "items": [$items],
        "total_items": ${ids.size},
        "auth": 1,
        "api_version": 3,
        "last_refreshed_on_time": $now
    }
    """.trimIndent()
    }

    // ------------------- 测试用例 -------------------

    @Test
    fun testGetFeeds() = runBlocking {
        val feeds = client.getFeeds()
        assertEquals(2, feeds.size)
        val feed1 = feeds.find { it.id == feedId1.toString() }
        assertNotNull(feed1)
        assertEquals("Feed 1", feed1?.name)
        assertEquals(listOf(groupId1.toString()), feed1?.groupIds)
        val feed2 = feeds.find { it.id == feedId2.toString() }
        assertNotNull(feed2)
        assertEquals("Feed 2", feed2?.name)
        assertTrue(feed2?.isSpark == true)
        assertEquals(listOf(groupId2.toString()), feed2?.groupIds)
    }

    @Test
    fun testGetGroups() = runBlocking {
        val groups = client.getGroups()
        assertEquals(2, groups.size)
        val group1 = groups.find { it.id == groupId1.toString() }
        assertNotNull(group1)
        assertEquals("Group 1", group1?.title)
        assertEquals(listOf(feedId1.toString()), group1?.feedIds)
        val group2 = groups.find { it.id == groupId2.toString() }
        assertNotNull(group2)
        assertEquals("Group 2", group2?.title)
        assertEquals(listOf(feedId2.toString()), group2?.feedIds)
    }

    @Test
    fun testGetItems() = runBlocking {
        val items = client.getItems()
        assertEquals(3, items.size)
        val item1 = items.find { it.id == itemId1.toString() }
        assertNotNull(item1)
        assertFalse(item1?.isRead ?: true)
        assertFalse(item1?.isStarred ?: true)
    }

    @Test
    fun testGetItemsWithSinceId() = runBlocking {
        val items = client.getItems(sinceId = 100L)
        assertEquals(2, items.size)
        assertTrue(items.all { it.id.toLong() > 100 })
    }

    @Test
    fun testGetItemsWithMaxId() = runBlocking {
        val items = client.getItems(maxId = 102L)
        assertEquals(2, items.size)
        assertTrue(items.all { it.id.toLong() < 102 })
    }

    @Test
    fun testGetItemsWithIds() = runBlocking {
        val items = client.getItems(withIds = listOf("100", "101"))
        assertEquals(2, items.size)
        assertTrue(items.any { it.id == "100" })
        assertTrue(items.any { it.id == "101" })
    }

    @Test
    fun testGetUnreadItemIds() = runBlocking {
        val ids = client.getUnreadItemIds()
        assertEquals(listOf("100", "102"), ids)
    }

    @Test
    fun testGetSavedItemIds() = runBlocking {
        val ids = client.getSavedItemIds()
        assertEquals(listOf("101"), ids)
    }

    @Test
    fun testGetFavicons() = runBlocking {
        val favicons = client.getFavicons()
        assertNotNull(favicons)
        assertEquals(2, favicons?.size)
        assertTrue(favicons?.any { f -> f.id == "1001" && f.data.startsWith("iVBORw0KGgo") } == true)
    }

    @Test
    fun testMarkItemAsRead() = runBlocking {
        // 初始未读
        val before = client.getItems(withIds = listOf("100"))
        assertEquals(1, before.size)
        assertFalse(before[0].isRead)

        client.markItemAsRead("100")

        // 覆盖 stub 返回已读
        stubFor(
            post(urlPathEqualTo("/"))
                .withQueryParam("api", equalTo(""))
                .withQueryParam("items", equalTo(""))
                .withQueryParam("with_ids", equalTo("100"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/json")
                        .withBody(
                            """
                        {
                            "items": [
                                {"id": $itemId1, "feed_id": $feedId1, "title": "Item 1", "author": "Author 1", "html": "<p>Content 1</p>", "url": "https://item1.com", "is_saved": 0, "is_read": 1, "created_on_time": ${now - 100}}
                            ],
                            "total_items": 1,
                            "auth": 1,
                            "api_version": 3,
                            "last_refreshed_on_time": $now
                        }
                        """.trimIndent()
                        )
                )
        )

        val after = client.getItems(withIds = listOf("100"))
        assertEquals(1, after.size)
        assertTrue(after[0].isRead)
    }

    @Test
    fun testSaveAndUnsaveItem() = runBlocking {
        // 初始未保存（默认 stub 返回 is_saved=0）
        val before = client.getItems(withIds = listOf("101"))
        assertFalse(before[0].isStarred)

        client.saveItem("101")
        // 添加 stub 返回已保存
        stubFor(
            post(urlPathEqualTo("/"))
                .withQueryParam("api", equalTo(""))
                .withQueryParam("items", equalTo(""))
                .withQueryParam("with_ids", equalTo("101"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/json")
                        .withBody(
                            """
                        {
                            "items": [
                                {"id": $itemId2, "feed_id": $feedId1, "title": "Item 2", "author": "Author 2", "html": "<p>Content 2</p>", "url": "https://item2.com", "is_saved": 1, "is_read": 0, "created_on_time": ${now - 50}}
                            ],
                            "total_items": 1,
                            "auth": 1,
                            "api_version": 3,
                            "last_refreshed_on_time": $now
                        }
                        """.trimIndent()
                        )
                )
        )
        val saved = client.getItems(withIds = listOf("101"))
        assertTrue(saved[0].isStarred)

        client.unsaveItem("101")
        // 添加 stub 返回未保存
        stubFor(
            post(urlPathEqualTo("/"))
                .withQueryParam("api", equalTo(""))
                .withQueryParam("items", equalTo(""))
                .withQueryParam("with_ids", equalTo("101"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/json")
                        .withBody(
                            """
                        {
                            "items": [
                                {"id": $itemId2, "feed_id": $feedId1, "title": "Item 2", "author": "Author 2", "html": "<p>Content 2</p>", "url": "https://item2.com", "is_saved": 0, "is_read": 0, "created_on_time": ${now - 50}}
                            ],
                            "total_items": 1,
                            "auth": 1,
                            "api_version": 3,
                            "last_refreshed_on_time": $now
                        }
                        """.trimIndent()
                        )
                )
        )
        val unsaved = client.getItems(withIds = listOf("101"))
        assertFalse(unsaved[0].isStarred)
    }

    @Test
    fun testMarkFeedAsRead() = runBlocking {
        client.markFeedAsRead("1", now)
        // 验证调用成功（不抛异常）
    }

    @Test
    fun testMarkGroupAsRead() = runBlocking {
        client.markGroupAsRead("10", now)
    }

    @Test
    fun testUnreadRecentlyRead() = runBlocking {
        client.unreadRecentlyRead()
    }

    @Test
    fun testGetLinks() = runBlocking {
        val links = client.getLinks()
        assertEquals(2, links.size)
        val link1 = links.find { it.id == "1001" }
        assertNotNull(link1)
        assertEquals(feedId1.toString(), link1?.feedId)
        assertEquals(itemId1.toString(), link1?.itemId)
        assertEquals(3.5f, link1?.temperature)
        assertTrue(link1?.isItem == true)
        assertTrue(link1?.isLocal == true)
        assertFalse(link1?.isSaved ?: true)
        assertEquals(listOf("100"), link1?.itemIds)
    }

    @Test
    fun testGetLinksWithPage() = runBlocking {
        val links = client.getLinks(page = 2)
        assertEquals(1, links.size)
        assertEquals("2001", links[0].id)
        assertEquals(feedId2.toString(), links[0].feedId)
        assertEquals(itemId3.toString(), links[0].itemId)
        assertEquals(4.0f, links[0].temperature)
    }
}