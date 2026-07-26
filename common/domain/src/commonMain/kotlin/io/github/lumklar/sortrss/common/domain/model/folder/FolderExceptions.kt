package io.github.lumklar.sortrss.common.domain.model.folder

import io.github.lumklar.sortrss.common.domain.shared.exception.DomainException

/** 文件夹不存在 */
class FolderNotFoundException : DomainException(FolderErrorCode.FOLDER_NOT_FOUND)

/** 文件夹名称为空 */
class FolderNameEmptyException : DomainException(FolderErrorCode.FOLDER_NAME_EMPTY)

/** 订阅源已在文件夹中 */
class FeedAlreadyInFolderException : DomainException(FolderErrorCode.FEED_ALREADY_IN_FOLDER)

/** 订阅源不在文件夹中 */
class FeedNotInFolderException : DomainException(FolderErrorCode.FEED_NOT_IN_FOLDER)

/** 不可将非本地订阅源添加到文件夹 */
class CannotAddFeedToFolderException : DomainException(FolderErrorCode.CANNOT_ADD_FEED)