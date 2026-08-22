package io.github.lumklar.sortrss.common.domain.model.user

interface UserRepository {
    fun findById(id: UserId): User?
    fun findByUsername(username: Username): User?
    fun existsByUsername(username: Username): Boolean
    fun save(user: User): User
}