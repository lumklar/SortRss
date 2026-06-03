package io.github.lumklar.sortrss.server.infrastructure.persistence.repository.jpa

import io.github.lumklar.sortrss.server.infrastructure.persistence.entity.UserPO
import org.springframework.data.jpa.repository.JpaRepository

interface UserJpaRepository : JpaRepository<UserPO, Long> {
    fun findByUsername(username: String?): UserPO?
    fun existsByUsername(username: String?): Boolean
}