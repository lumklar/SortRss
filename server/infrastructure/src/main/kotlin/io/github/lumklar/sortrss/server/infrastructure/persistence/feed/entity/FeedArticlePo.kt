package io.github.lumklar.sortrss.server.infrastructure.persistence.feed.entity

import jakarta.persistence.Column
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant

@Entity
@Table(name = "feed_articles")
class FeedArticlePo(
    @EmbeddedId
    var id: FeedArticlePk? = null,

    // 新增：数据源中的文章分组ID（或文章在外部系统中的唯一标识）
    @Column(name = "source_article_id", length = 255)
    var sourceArticleId: String? = null,

    @CreationTimestamp
    @Column(name = "gmt_create", nullable = false, updatable = false)
    var gmtCreate: Instant? = null,

    @UpdateTimestamp
    @Column(name = "gmt_modify", nullable = false)
    var gmtModify: Instant? = null
)