package io.github.lumklar.sortrss.common.domain.model.user

import io.github.lumklar.sortrss.common.domain.model.user.User


interface UserRepository {
    fun findById(id: Long): User?
    fun findByUsername(username: String): User?
    fun existsByUsername(username: String): Boolean
    fun save(user: User): User
}