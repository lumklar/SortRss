package io.github.lumklar.sortrss.common.domain.repository

import io.github.lumklar.sortrss.common.domain.model.User

interface UserRepository {
    fun findById(id: Long): User?
    fun findByUsername(username: String): User?
    fun existsByUsername(username: String): Boolean
    fun save(user: User): User
}