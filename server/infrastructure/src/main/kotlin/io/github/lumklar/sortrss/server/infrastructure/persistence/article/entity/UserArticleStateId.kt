package io.github.lumklar.sortrss.server.infrastructure.persistence.article.entity

import jakarta.persistence.Embeddable
import java.util.*

@Embeddable
data class UserArticleStateId(
    val userId: UUID,
    val articleId: UUID
) : java.io.Serializable
