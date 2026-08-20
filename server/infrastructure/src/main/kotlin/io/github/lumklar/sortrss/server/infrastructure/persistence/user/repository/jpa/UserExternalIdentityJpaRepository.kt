package io.github.lumklar.sortrss.server.infrastructure.persistence.user.repository.jpa

import io.github.lumklar.sortrss.common.domain.model.user.ExternalProvider
import io.github.lumklar.sortrss.server.infrastructure.persistence.user.entity.UserExternalIdentityPO
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserExternalIdentityJpaRepository : JpaRepository<UserExternalIdentityPO, UUID> {
    fun findByUserIdAndProvider(userId: UUID, provider: ExternalProvider): List<UserExternalIdentityPO>
    fun findByProviderAndSubject(provider: ExternalProvider, subject: String): UserExternalIdentityPO?
    fun findByUserId(userId: UUID): List<UserExternalIdentityPO>
    fun deleteByUserIdAndProvider(userId: UUID, provider: ExternalProvider)
}