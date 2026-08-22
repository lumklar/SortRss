package io.github.lumklar.sortrss.server.infrastructure.persistence.user.repository

import io.github.lumklar.sortrss.common.domain.model.user.User
import io.github.lumklar.sortrss.common.domain.model.user.UserId
import io.github.lumklar.sortrss.common.domain.model.user.UserRepository
import io.github.lumklar.sortrss.common.domain.model.user.Username
import io.github.lumklar.sortrss.server.infrastructure.persistence.user.convert.toDomain
import io.github.lumklar.sortrss.server.infrastructure.persistence.user.convert.toPO
import io.github.lumklar.sortrss.server.infrastructure.persistence.user.repository.jpa.UserJpaRepository
import org.springframework.stereotype.Repository
import kotlin.uuid.toJavaUuid

@Repository
class UserRepositoryImpl(
    private val jpaRepository: UserJpaRepository   // 只需注入 JPA 接口
) : UserRepository {

    override fun findById(id: UserId): User? {
        return jpaRepository.findById(id.value.toJavaUuid()).orElse(null)?.toDomain()   // 直接调用扩展函数
    }

    override fun findByUsername(username: Username): User? {
        return jpaRepository.findByUsername(username.value)?.toDomain()
    }

    override fun existsByUsername(username: Username): Boolean {
        return jpaRepository.existsByUsername(username.value)
    }

    override fun save(user: User): User {
        val po = user.toPO()          // 调用扩展函数生成 PO
        val savedPo = jpaRepository.save(po)
        return savedPo.toDomain()     // 转回领域模型
    }
}