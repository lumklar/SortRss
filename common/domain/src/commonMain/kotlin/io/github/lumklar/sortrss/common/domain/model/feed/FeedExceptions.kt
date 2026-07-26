package io.github.lumklar.sortrss.common.domain.model.feed

import io.github.lumklar.sortrss.common.domain.shared.exception.DomainException

/** 订阅源不存在 */
class FeedNotFoundException : DomainException(FeedErrorCode.FEED_NOT_FOUND)

/** 订阅源 URL 为空 */
class FeedUrlEmptyException : DomainException(FeedErrorCode.FEED_URL_EMPTY)

/** 订阅源已存在（重复添加） */
class FeedAlreadyExistsException : DomainException(FeedErrorCode.FEED_ALREADY_EXISTS)

/** 尝试编辑一个不可编辑的订阅源（非本地 OPML 源） */
class FeedNotEditableException : DomainException(FeedErrorCode.FEED_NOT_EDITABLE)

/** 订阅源标题为空 */
class FeedTitleEmptyException : DomainException(FeedErrorCode.FEED_TITLE_EMPTY)