package io.github.lumklar.sortrss.common.domain.model.folder

import kotlin.jvm.JvmInline

/**
 * 文件夹名称值对象。
 * 确保名称非空、非空白且已修剪。
 */
@JvmInline
value class FolderName private constructor(val value: String) {
    init {
        require(value.isNotBlank()) { throw FolderNameEmptyException() }
        require(value.length in 1..10) { "用户名长度必须在 1 到 10 之间" }
    }

    companion object {
        fun from(name: String): FolderName {
            return FolderName(name.trim())
        }
    }
}
