package io.github.lumklar.sortrss.server.application.pojo.user.query
/**
 * 用户查询条件，当前仅使用 username，
 */
data class UserQuery(
    val username: String? = null,
)
