package io.github.lumklar.sortrss.common.domain.model.folder

import io.github.lumklar.sortrss.common.domain.shared.exception.DomainException

/** 文件夹不存在 */
class FolderNotFoundException(
    message: String = FolderErrorCode.FOLDER_NOT_FOUND.msg,
) : DomainException(
    domainCode = FolderErrorCode.FOLDER_NOT_FOUND,
    message = message
)

/** 文件夹名称为空 */
class FolderNameEmptyException(
    message: String = FolderErrorCode.FOLDER_NAME_EMPTY.msg,
) : DomainException(
    domainCode = FolderErrorCode.FOLDER_NAME_EMPTY,
    message = message
)

/** 订阅源已在文件夹中 */
class FeedAlreadyInFolderException(
    message: String = FolderErrorCode.FEED_ALREADY_IN_FOLDER.msg,
) : DomainException(
    domainCode = FolderErrorCode.FEED_ALREADY_IN_FOLDER,
    message = message
)

/** 订阅源不在文件夹中 */
class FeedNotInFolderException(
    message: String = FolderErrorCode.FEED_NOT_IN_FOLDER.msg,
) : DomainException(
    domainCode = FolderErrorCode.FEED_NOT_IN_FOLDER,
    message = message
)

/** 不可将非本地订阅源添加到文件夹 */
class CannotAddFeedToFolderException(
    message: String = FolderErrorCode.CANNOT_ADD_FEED.msg,
) : DomainException(
    domainCode = FolderErrorCode.CANNOT_ADD_FEED,
    message = message
)

/** 订阅源不属于任何文件夹 */
class FeedNotInAnyFolderException(
    message: String = FolderErrorCode.FEED_NOT_IN_ANY_FOLDER.msg,
) : DomainException(
    domainCode = FolderErrorCode.FEED_NOT_IN_ANY_FOLDER,
    message = message
)

/** 无法将订阅源移动到目标文件夹（通常因为类型不匹配或已在目标文件夹中） */
class CannotMoveFeedException(
    message: String = FolderErrorCode.CANNOT_MOVE_FEED.msg,
) : DomainException(
    domainCode = FolderErrorCode.CANNOT_MOVE_FEED,
    message = message
)

/** 无法将订阅源从文件夹中移除（通常因为不是本地订阅源） */
class CannotRemoveFeedException(
    message: String = FolderErrorCode.CANNOT_REMOVE_FEED.msg,
) : DomainException(
    domainCode = FolderErrorCode.CANNOT_REMOVE_FEED,
    message = message
)