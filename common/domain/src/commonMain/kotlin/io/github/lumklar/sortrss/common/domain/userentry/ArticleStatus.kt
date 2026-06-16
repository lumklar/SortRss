package io.github.lumklar.sortrss.common.domain.userentry

enum class ArticleStatus {
    UNREAD,
    READ,
    STARRED, // 收藏
    ARCHIVED;

    fun toggleRead(): ArticleStatus = when (this) {
        UNREAD -> READ
        READ -> UNREAD
        STARRED -> STARRED // 收藏状态不随已读切换
        ARCHIVED -> ARCHIVED
    }

    fun toggleStar(): ArticleStatus = when (this) {
        STARRED -> READ
        else -> STARRED
    }
}