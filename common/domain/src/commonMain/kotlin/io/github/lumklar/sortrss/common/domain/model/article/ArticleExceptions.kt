package io.github.lumklar.sortrss.common.domain.model.article

import io.github.lumklar.sortrss.common.domain.shared.exception.DomainException

/** 文章不存在 */
class ArticleNotFoundException : DomainException(ArticleErrorCode.ARTICLE_NOT_FOUND)

/** 文章标题为空 */
class ArticleTitleEmptyException : DomainException(ArticleErrorCode.ARTICLE_TITLE_EMPTY)

/** 文章链接为空 */
class ArticleLinkEmptyException : DomainException(ArticleErrorCode.ARTICLE_LINK_EMPTY)