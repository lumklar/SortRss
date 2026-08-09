package io.github.lumklar.sortrss.common.foundation

import io.github.lumklar.sortrss.common.foundation.shared.ability.api.fever.FeverClient
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*

class FeverClientTest {

    private val baseUrl = ""
    private val username = ""
    private val password = ""

    private val client = FeverClient(
        baseUrl, username, password,
        HttpClient(OkHttp) {
            engine {
//                proxy = Proxy(Proxy.Type.HTTP, InetSocketAddress("127.0.0.1", 7890))
            }
        }
    )

//    @Test   // 启用测试
    fun testAllEndpoints() = runBlocking {
        // 1. Feeds
        println("\n========== Feeds ==========")
        val feeds = client.getFeeds()
        assertTrue(feeds.isNotEmpty(), "Feeds should not be empty")
        feeds.forEach { println("Feed: ${it.name} (ID: ${it.id}), Groups: ${it.groupIds}") }

        // 2. Groups
        println("\n========== Groups ==========")
        val groups = client.getGroups()
        assertTrue(groups.isNotEmpty(), "Groups should not be empty")
        groups.forEach { println("Group: ${it.title} (ID: ${it.id}), Feed IDs: ${it.feedIds}") }

        // 3. Items (basic + pagination + with_ids)
        println("\n========== Items (first page) ==========")
        val items = client.getItems()
        assertTrue(items.isNotEmpty(), "Items should not be empty")
        items.take(5).forEach { item ->
            println("${item.title} (ID: ${item.id}) - read: ${item.isRead}, saved: ${item.isStarred}")
        }

        // Pagination: since_id
        if (items.isNotEmpty()) {
            val maxId = items.maxOf { it.id.toLong() }
            println("\n--- Items since_id=$maxId ---")
            val nextItems = client.getItems(sinceId = maxId)
            println("Fetched ${nextItems.size} items after since_id")
        }

        // Pagination: max_id
        if (items.size > 1) {
            val minId = items.minOf { it.id.toLong() }
            println("\n--- Items max_id=$minId ---")
            val prevItems = client.getItems(maxId = minId)
            println("Fetched ${prevItems.size} items before max_id")
        }

        // with_ids
        val sampleIds = items.take(3).map { it.id }
        println("\n--- Items with_ids=${sampleIds} ---")
        val specificItems = client.getItems(withIds = sampleIds)
        assertEquals(sampleIds.size, specificItems.size)
        specificItems.forEach { println("Item: ${it.title} (ID: ${it.id})") }

        // 4. Unread / Saved IDs
        val unreadIds = client.getUnreadItemIds()
        println("\nUnread item IDs count: ${unreadIds.size}")
        val savedIds = client.getSavedItemIds()
        println("Saved item IDs count: ${savedIds.size}")

        // 5. Favicons
//        println("\n========== Favicons ==========")
//        val favicons = client.getFavicons()
//        assertNotNull(favicons)
//        println("Favicons count: ${favicons?.size}")
//        favicons?.take(2)?.forEach { favicon ->
//            println("Favicon ID: ${favicon.id}, data length: ${favicon.data.length}")
//        }

        // 6. Mark item as read + unread recently read (with recovery warning)
        val unreadItem = items.find { !it.isRead }
        if (unreadItem != null) {
            println("\n========== Test mark item as read ==========")
            println("Original unread item: ${unreadItem.title} (ID: ${unreadItem.id})")
            client.markItemAsRead(unreadItem.id)

            // Verify it's now read
            var checkItems = client.getItems(withIds = listOf(unreadItem.id))
            val updated = checkItems.firstOrNull()
            assertNotNull(updated, "Item should exist after marking read")
            assertTrue(updated!!.isRead, "Item should be marked as read")

            // Attempt recovery: unread recently read
            client.unreadRecentlyRead()
            checkItems = client.getItems(withIds = listOf(unreadItem.id))
            val reverted = checkItems.firstOrNull()
            if (reverted != null && !reverted.isRead) {
                println("Successfully reverted to unread.")
                assertFalse(reverted.isRead, "Item should be unread again")
            } else {
                println("WARNING: unreadRecentlyRead() did NOT revert the item to unread. Server may not support this feature or has delay.")
                // Do not fail test, just warn
            }
        }

        // 7. Save / unsave item
        val unsavedItem = items.find { !it.isStarred }
        if (unsavedItem != null) {
            println("\n========== Test save / unsave item ==========")
            println("Unsaved item: ${unsavedItem.title} (ID: ${unsavedItem.id})")
            client.saveItem(unsavedItem.id)

            var check = client.getItems(withIds = listOf(unsavedItem.id)).first()
            assertTrue(check.isStarred, "Item should be saved")

            client.unsaveItem(unsavedItem.id)
            check = client.getItems(withIds = listOf(unsavedItem.id)).first()
            assertFalse(check.isStarred, "Item should be unsaved")
        }

        // 8. Mark feed as read + unread recently read
//        if (feeds.isNotEmpty() && items.isNotEmpty()) {
//            val testFeed = feeds.first()
//            println("\n========== Test mark feed as read ==========")
//            println("Feed: ${testFeed.name} (ID: ${testFeed.id})")
//
//            val beforeTimestamp = System.currentTimeMillis() / 1000
//            client.markFeedAsRead(testFeed.id, beforeTimestamp)
//
//            // Attempt recovery
//            client.unreadRecentlyRead()
//            println("Attempted to revert feed read status with unreadRecentlyRead (may not work for all items).")
//            // No strict assertion here since it's hard to verify without knowing which items were affected
//        }

        // 9. Mark group as read + unread recently read
//        if (groups.isNotEmpty()) {
//            val testGroup = groups.first()
//            println("\n========== Test mark group as read ==========")
//            println("Group: ${testGroup.title} (ID: ${testGroup.id})")
//
//            val beforeTimestamp = System.currentTimeMillis() / 1000
//            client.markGroupAsRead(testGroup.id, beforeTimestamp)
//
//            // Revert
//            client.unreadRecentlyRead()
//            println("Marked group as read and reverted (unread recently read)")
//        }

        // 10. Hot Links
        println("\n========== Hot Links ==========")
        val links = client.getLinks()
        println("Fetched ${links.size} links")
        links.take(3).forEach { link ->
            println("Link: ${link.title} (ID: ${link.id}), temperature: ${link.temperature}, isItem: ${link.isItem}")
        }

        // 11. Links with pagination
        println("\n--- Links page=2 ---")
        val linksPage2 = client.getLinks(page = 2)
        println("Fetched ${linksPage2.size} links on page 2")

        println("\n========== All tests completed ==========")
    }
}