package io.github.lumklar.sortrss.common.domain.model.feed

import io.github.lumklar.sortrss.common.domain.shared.exception.DomainException

/** 订阅源不存在 */
class FeedNotFoundException(
    message: String = FeedErrorCode.FEED_NOT_FOUND.msg,
) : DomainException(
    domainCode = FeedErrorCode.FEED_NOT_FOUND,
    message = message
)

/** 订阅源 URL 为空 */
class FeedUrlEmptyException(
    message: String = FeedErrorCode.FEED_URL_EMPTY.msg,
) : DomainException(
    domainCode = FeedErrorCode.FEED_URL_EMPTY,
    message = message
)

/** 订阅源已存在（重复添加） */
class FeedAlreadyExistsException(
    message: String = FeedErrorCode.FEED_ALREADY_EXISTS.msg,
) : DomainException(
    domainCode = FeedErrorCode.FEED_ALREADY_EXISTS,
    message = message
)

/** 尝试编辑一个不可编辑的订阅源（非本地 OPML 源） */
class FeedNotEditableException(
    message: String = FeedErrorCode.FEED_NOT_EDITABLE.msg,
) : DomainException(
    domainCode = FeedErrorCode.FEED_NOT_EDITABLE,
    message = message
)

/** 订阅源标题为空 */
class FeedTitleEmptyException(
    message: String = FeedErrorCode.FEED_TITLE_EMPTY.msg,
) : DomainException(
    domainCode = FeedErrorCode.FEED_TITLE_EMPTY,
    message = message
)