package io.github.lumklar.sortrss.server.infrastructure.persistence.article.entity

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.util.*

@Embeddable
data class UserArticleStateId(
    @Column(name = "user_id", nullable = false)
    val userId: UUID,

    @Column(name = "article_id", nullable = false)
    val articleId: UUID
) : java.io.Serializable