package io.github.lumklar.sortrss.common.domain.shared.valueobjects

import kotlin.jvm.JvmInline

@JvmInline
value class Url private constructor(val value: String) {
    init {
        require(value.isNotBlank()) { "URL cannot be blank" }

        // 纯 Kotlin 正则，全平台通用
        // 较宽松但实用的 HTTP/HTTPS 校验
        val urlPattern = Regex(
            "^https?://[a-zA-Z0-9\\-.]+(\\.[a-zA-Z]{2,})(:[0-9]+)?(/.*)?$",
            RegexOption.IGNORE_CASE
        )
        require(urlPattern.matches(value)) {
            "Invalid URL format: '$value'. Must start with http:// or https://"
        }

        // 额外可选：防止空格或特殊控制字符
        require(value.indexOfAny(charArrayOf(' ', '\n', '\r')) == -1) {
            "URL cannot contain whitespace"
        }
    }

    override fun toString(): String = value

    companion object {
        fun fromString(raw: String): Url = Url(raw.trim())
    }
}