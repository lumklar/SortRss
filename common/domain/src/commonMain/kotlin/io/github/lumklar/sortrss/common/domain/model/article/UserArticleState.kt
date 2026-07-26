package io.github.lumklar.sortrss.common.domain.model.article
import io.github.lumklar.sortrss.common.domain.model.user.UserId
import kotlin.time.Instant

/**
 * 用户与文章之间的关联对象，记录阅读状态与星标等用户维度的数据。
 */
data class UserArticleState(
    val userId: UserId,
    val articleId: ArticleId,
    val read: Boolean? = false,
    val starred: Boolean = false,
) {
    /** 标记为已读 */
    fun markRead(): UserArticleState = copy(read = true)

    /** 标记为未读 */
    fun markUnread(): UserArticleState = copy(read = false)

    /**
     * 标记回null
     */
    fun clearRead(): UserArticleState = copy(read = null)

    /** 切换星标状态 */
    fun toggleStar(): UserArticleState = copy(starred = !starred)
}