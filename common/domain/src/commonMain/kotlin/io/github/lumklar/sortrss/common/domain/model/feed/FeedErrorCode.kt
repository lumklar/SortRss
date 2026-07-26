package io.github.lumklar.sortrss.common.domain.model.feed

import io.github.lumklar.sortrss.common.domain.shared.error.DomainErrorCode

enum class FeedErrorCode(
    override val code: Int,
    override val msg: String
) : DomainErrorCode {
    FEED_NOT_FOUND(4001, "订阅源不存在"),
    FEED_URL_EMPTY(4002, "订阅源 URL 不能为空"),
    FEED_ALREADY_EXISTS(4003, "订阅源已存在"),
    FEED_NOT_EDITABLE(4004, "该订阅源不允许编辑（仅本地订阅源可编辑）"),
    FEED_TITLE_EMPTY(4005, "订阅源标题不能为空")
}