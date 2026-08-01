package io.github.lumklar.sortrss.common.domain.model.folder

import io.github.lumklar.sortrss.common.domain.shared.error.DomainErrorCode

enum class FolderErrorCode(
    override val code: Int,
    override val msg: String
) : DomainErrorCode {
    FOLDER_NOT_FOUND(5001, "文件夹不存在"),
    FOLDER_NAME_EMPTY(5002, "文件夹名称不能为空"),
    FEED_ALREADY_IN_FOLDER(5003, "订阅源已在该文件夹中"),
    FEED_NOT_IN_FOLDER(5004, "订阅源不在该文件夹中"),
    CANNOT_ADD_FEED(5005, "无法将该订阅源添加到文件夹（仅本地订阅源可移动）"),
    FEED_NOT_IN_ANY_FOLDER(5006, "订阅源不在任何文件夹中"),
    CANNOT_MOVE_FEED(5007, "无法移动订阅源（仅本地订阅源可移动，且目标文件夹必须为本地类型）"),
    CANNOT_REMOVE_FEED(5008, "无法移除订阅源（仅本地订阅源可移出文件夹）")
}