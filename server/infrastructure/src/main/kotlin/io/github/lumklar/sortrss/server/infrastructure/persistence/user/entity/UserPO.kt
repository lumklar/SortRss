package io.github.lumklar.sortrss.server.infrastructure.persistence.user.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.*

@Entity
@Table(name = "users")
final class UserPO {
    @Id
    var id: UUID? = null

    @Column(nullable = false, unique = true, length = 20)
    var username: String = ""

    @Column(nullable = false)
    var password: String = ""
}