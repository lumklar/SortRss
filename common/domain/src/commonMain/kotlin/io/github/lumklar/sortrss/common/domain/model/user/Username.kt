package io.github.lumklar.sortrss.common.domain.model.user

import kotlin.jvm.JvmInline
import kotlin.uuid.Uuid

/**
 * 用户名值对象，封装验证和展示逻辑。
 */
@JvmInline
value class Username private constructor(val value: String) {
    // 基础安全校验：所有用户名必须满足的底线
    init {
        require(value.isNotBlank()) { "用户名不能为空" }
        require(value.length in 1..34) { "用户名长度必须在 1 到 34 之间" }
        require(value.matches(Regex("^[a-zA-Z0-9_-]+$"))) {
            "用户名只能包含字母、数字、下划线和连字符"
        }
    }

    companion object {
        /**
         * 业务用户名（普通注册、用户主动修改）
         * - 长度 3~20
         * - 仅允许字母、数字、下划线
         */
        internal fun fromBusinessString(raw: String): Username {
            val trimmed = raw.trim()
            val username = Username(trimmed) // 先经过基础校验
            require(trimmed.length in 3..20) {
                "用户名长度必须在 3 到 20 之间"
            }
            require(trimmed.matches(Regex("^[a-zA-Z0-9_]+$"))) {
                "用户名只能包含字母、数字和下划线"
            }
            return username
        }

        internal fun fromUuid(uuid: Uuid): Username {
            return Username("u_" + uuid.toString().replace("-", ""))
        }

        /**
         * 从数据库重建（不额外施加业务规则，信任存储数据）
         */
        internal fun fromAny(raw: String): Username {
            return Username(raw.trim())
        }
    }
}