package io.github.lumklar.sortrss.server.infrastructure.persistence.repository

import io.github.lumklar.sortrss.common.domain.model.entity.User
import io.github.lumklar.sortrss.common.domain.repository.UserRepository
import io.github.lumklar.sortrss.server.infrastructure.persistence.convert.UserConverter
import io.github.lumklar.sortrss.server.infrastructure.persistence.repository.jpa.UserJpaRepository
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryImpl(
    private val jpaRepository: UserJpaRepository,   // Spring Data JPA 接口
    private val converter: UserConverter
) : UserRepository {

    override fun findById(id: Long): User? {
        return jpaRepository.findById(id).orElse(null)?.let { converter.toDomain(it) }
    }

    override fun findByUsername(username: String): User? {
        return jpaRepository.findByUsername(username)?.let { converter.toDomain(it) }
    }

    override fun existsByUsername(username: String): Boolean {
        return jpaRepository.existsByUsername(username)
    }

    override fun save(user: User): User {
        var po = converter.toPO(user)
        po = jpaRepository.save(po)
        return converter.toDomain(po)
    }
}