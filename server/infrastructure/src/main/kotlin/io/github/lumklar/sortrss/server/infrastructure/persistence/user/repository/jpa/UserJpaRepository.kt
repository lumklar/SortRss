package io.github.lumklar.sortrss.server.infrastructure.persistence.user.entity.repository.jpa

import io.github.lumklar.sortrss.server.infrastructure.persistence.user.entity.UserPO
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UserJpaRepository : JpaRepository<UserPO, UUID> {
    fun findByUsername(username: String?): UserPO?
    fun existsByUsername(username: String?): Boolean
}