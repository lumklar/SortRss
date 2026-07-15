package io.github.lumklar.sortrss.common.domain.userentry

/**
 * 用户对某篇文章的阅读状态。
 * 包含四种状态，并提供状态切换的业务行为。
 */
enum class ArticleStatus {
    UNREAD,   // 未读
    READ,     // 已读
    STARRED,  // 已收藏（星标）
    ARCHIVED; // 已归档

    /**
     * 切换已读/未读状态。
     * - 收藏状态不随切换改变（保持 STARRED）。
     * - 归档状态不变。
     */
    fun toggleRead(): ArticleStatus = when (this) {
        UNREAD -> READ
        READ -> UNREAD
        STARRED -> STARRED
        ARCHIVED -> ARCHIVED
    }

    /**
     * 切换收藏状态。
     * - 若当前为 STARRED，则取消收藏变为 READ。
     * - 其他状态（包括 UNREAD/READ/ARCHIVED）均置为 STARRED。
     */
    fun toggleStar(): ArticleStatus = when (this) {
        STARRED -> READ
        else -> STARRED
    }
}