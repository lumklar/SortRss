package io.github.lumklar.sortrss.server.infrastructure.persistence.user.repository

import io.github.lumklar.sortrss.common.domain.model.user.ExternalIdentity
import io.github.lumklar.sortrss.common.domain.model.user.ExternalIdentityRepository
import io.github.lumklar.sortrss.common.domain.model.user.ExternalProvider
import io.github.lumklar.sortrss.common.domain.model.user.UserId
import io.github.lumklar.sortrss.server.infrastructure.persistence.user.convert.toDomain
import io.github.lumklar.sortrss.server.infrastructure.persistence.user.convert.toPersistence
import io.github.lumklar.sortrss.server.infrastructure.persistence.user.repository.jpa.UserExternalIdentityJpaRepository
import org.springframework.stereotype.Repository
import kotlin.uuid.toJavaUuid

@Repository
class ExternalIdentityRepositoryImpl(
    private val jpaRepository: UserExternalIdentityJpaRepository
) : ExternalIdentityRepository {

    override fun findByProviderAndSubject(
        provider: ExternalProvider,
        subject: String
    ): ExternalIdentity? {
        return jpaRepository.findByProviderAndSubject(provider, subject)
            ?.let { it.toDomain() }
    }

    override fun findByUserId(userId: UserId): List<ExternalIdentity> {
        return jpaRepository.findByUserId(userId.value.toJavaUuid())
            .map { it.toDomain() }
    }

    override fun save(identity: ExternalIdentity) {
        jpaRepository.save(identity.toPersistence())
    }

    override fun deleteByUserIdAndProvider(userId: UserId, provider: ExternalProvider) {
        jpaRepository.deleteByUserIdAndProvider(userId.value.toJavaUuid(), provider)
    }
}
