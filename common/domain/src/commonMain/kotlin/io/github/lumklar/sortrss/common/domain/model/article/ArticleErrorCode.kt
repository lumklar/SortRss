package io.github.lumklar.sortrss.common.domain.model.article

import io.github.lumklar.sortrss.common.domain.shared.error.DomainErrorCode

enum class ArticleErrorCode(
    override val code: Int,
    override val msg: String
) : DomainErrorCode {
    ARTICLE_NOT_FOUND(6001, "文章不存在"),
    ARTICLE_TITLE_EMPTY(6002, "文章标题不能为空"),
    ARTICLE_LINK_EMPTY(6003, "文章链接不能为空")
}