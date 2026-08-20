package io.github.lumklar.sortrss.common.domain.model.user

interface ExternalIdentityRepository {
    fun findByProviderAndSubject(provider: ExternalProvider, subject: String): ExternalIdentity?
    fun findByUserId(userId: UserId): List<ExternalIdentity>
    fun save(identity: ExternalIdentity)
    fun deleteByUserIdAndProvider(userId: UserId, provider: ExternalProvider)
}
