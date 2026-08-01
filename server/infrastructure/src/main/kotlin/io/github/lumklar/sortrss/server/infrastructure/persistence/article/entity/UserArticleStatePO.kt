package io.github.lumklar.sortrss.server.infrastructure.persistence.article.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.Instant;

@Entity
@Table(
    name = "user_article_state",
    uniqueConstraints = [UniqueConstraint(columnNames = ["userId", "articleId"])]
)
class UserArticleStatePO(
    @EmbeddedId
    var id: UserArticleStateId? = null,

    @Column(nullable = true)
    var read: Boolean? = false,

    @Column(nullable = false)
    var starred: Boolean? = false,

    @CreationTimestamp
    @Column(name = "gmt_create", nullable = false, updatable = false)
    var gmtCreate: Instant? = null,

    @UpdateTimestamp
    @Column(name = "gmt_modify", nullable = false)
    var gmtModify: Instant? = null
)