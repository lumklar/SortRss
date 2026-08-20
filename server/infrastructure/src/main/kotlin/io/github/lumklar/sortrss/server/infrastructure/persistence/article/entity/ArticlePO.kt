package io.github.lumklar.sortrss.server.infrastructure.persistence.article.entity;

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.util.*

@Entity
@Table(
    name = "article",
    uniqueConstraints = [UniqueConstraint(columnNames = ["guid"])],
    indexes = [
        Index(name = "idx_article_published_at", columnList = "published_at")
    ]
)
class ArticlePO(
    @Id
    @Column(name = "id", nullable = false)
    var id: UUID? = null,

    @Column(name = "title", nullable = false, length = 500)
    var title: String? = null,

    @Column(name = "author", length = 255)
    var author: String? = null,

    @Column(name = "summary", columnDefinition = "TEXT")
    var summary: String? = null,

    @Column(name = "content", columnDefinition = "MEDIUMTEXT")
    var content: String? = null,

    @Column(name = "link", nullable = false, length = 2048)
    var link: String? = null,

    @Column(name = "published_at", nullable = false)
    var publishedAt: Instant? = null,

    @Column(name = "guid", length = 512)
    var guid: String? = null,

    @Column(name = "content_modified_time")
    var contentModifiedTime: Instant? = null,

    @CreationTimestamp
    @Column(name = "gmt_create", nullable = false, updatable = false)
    var gmtCreate: Instant? = null,

    @UpdateTimestamp
    @Column(name = "gmt_modify", nullable = false)
    var gmtModify: Instant? = null
)