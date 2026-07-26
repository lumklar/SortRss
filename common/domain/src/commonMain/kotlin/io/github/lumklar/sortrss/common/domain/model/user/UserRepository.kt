package io.github.lumklar.sortrss.common.domain.model.user

import kotlin.uuid.Uuid

interface UserRepository {
    fun findById(id: Uuid): User?
    fun findByUsername(username: String): User?
    fun existsByUsername(username: String): Boolean
    fun save(user: User): User
}