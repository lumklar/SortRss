package io.github.lumklar.sortrss.common.domain.model.user

import kotlin.jvm.JvmInline

/**
 * 用户名值对象，封装验证和展示逻辑。
 */
@JvmInline
value class Username private constructor(val value: String) {
    init {
        require(value.isNotBlank()) { "用户名不能为空" }
        require(value.length in 3..20) { "用户名长度必须在 3 到 20 之间" }
        // 可添加其他规则，如仅允许字母数字下划线
        require(value.matches(Regex("^[a-zA-Z0-9_]+$"))) { "用户名只能包含字母、数字和下划线" }
    }

    companion object {
        fun fromString(raw: String): Username = Username(raw.trim())
    }
}