package io.github.lumklar.sortrss.server.infrastructure.persistence.user.entity;

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.util.*

@Entity
@Table(
    name = "users",
    uniqueConstraints = [UniqueConstraint(columnNames = ["username"])]
)
class UserPO(
    @Id
    @Column(nullable = false)
    var id: UUID? = null,

    @Column(nullable = false, length = 255)
    var username: String? = null,

    @Column(nullable = false, length = 255)
    var password: String? = null,

    @CreationTimestamp
    @Column(name = "gmt_create", nullable = false, updatable = false)
    var gmtCreate: Instant? = null,

    @UpdateTimestamp
    @Column(name = "gmt_modify", nullable = false)
    var gmtModify: Instant? = null
)