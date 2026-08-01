package io.github.lumklar.sortrss.common.domain.model.article

import io.github.lumklar.sortrss.common.domain.shared.exception.DomainException

/** 文章不存在 */
class ArticleNotFoundException(
    message: String = ArticleErrorCode.ARTICLE_NOT_FOUND.msg,
) : DomainException(
    domainCode = ArticleErrorCode.ARTICLE_NOT_FOUND,
    message = message
)

/** 文章标题为空 */
class ArticleTitleEmptyException(
    message: String = ArticleErrorCode.ARTICLE_TITLE_EMPTY.msg,
) : DomainException(
    domainCode = ArticleErrorCode.ARTICLE_TITLE_EMPTY,
    message = message
)

/** 文章链接为空 */
class ArticleLinkEmptyException(
    message: String = ArticleErrorCode.ARTICLE_LINK_EMPTY.msg,
) : DomainException(
    domainCode = ArticleErrorCode.ARTICLE_LINK_EMPTY,
    message = message
)