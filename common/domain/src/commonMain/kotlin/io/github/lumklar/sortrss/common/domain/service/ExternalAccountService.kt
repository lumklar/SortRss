package io.github.lumklar.sortrss.common.domain.service

import io.github.lumklar.sortrss.common.domain.model.user.*
import io.github.lumklar.sortrss.common.domain.shared.ability.IdGenerator

/**
 * 领域服务：负责外部账号的注册、登录、绑定与解绑。
 * 不包含 OAuth 协议细节，只处理业务规则。
 */
class ExternalAccountService(
    private val userIdGenerator: IdGenerator<UserId>,
    private val externalIdGenerator: IdGenerator<ExternalIdentityId>,
    private val userRepository: UserRepository,
    private val externalIdentityRepository: ExternalIdentityRepository
) {

    /**
     * 处理第三方登录/注册/绑定。
     *
     * @param provider 第三方提供方
     * @param subject 第三方用户唯一标识
     * @param currentUserId 当前已登录用户ID，用于绑定场景；为空表示未登录
     * @return 认证结果，包含用户ID和是否新用户
     */
    fun registerOrLogin(
        provider: ExternalProvider,
        subject: String,
        currentUserId: UserId? = null
    ): AuthResult {
        // 1. 检查第三方身份是否已绑定
        val existing = externalIdentityRepository.findByProviderAndSubject(provider, subject)
        if (existing != null) {
            return AuthResult(existing.userId, isNewUser = false)
        }

        // 2. 已登录用户绑定操作
        if (currentUserId != null) {
            // 获取用户，若为匿名则升级
            val user = userRepository.findById(currentUserId)
                ?: throw UserNotFoundException()

            // 检查是否已绑定同 provider 的其他账号
            val userIdentities = externalIdentityRepository.findByUserId(currentUserId)
            require(userIdentities.none { it.provider == provider }) {
                "该用户已绑定 ${provider} 账号"
            }

            // 若用户是匿名，升级为 EXTERNAL
            if (user.registrationSource == RegistrationSource.ANONYMOUS) {
                user.upgradeFromAnonymousToExternal()
                userRepository.save(user)  // 注意需要持久化
            }

            val identity = ExternalIdentity.create(
                id = externalIdGenerator.next(),
                userId = currentUserId,
                provider = provider,
                subject = subject,
            )
            externalIdentityRepository.save(identity)
            return AuthResult(currentUserId, isNewUser = false)
        }

        // 3. 未登录且第三方身份未绑定：创建新用户（EXTERNAL）
        val newUserId = userIdGenerator.next()
        val user = User.registerExternal(id = newUserId)
        userRepository.save(user)

        val identity = ExternalIdentity.create(
            id = externalIdGenerator.next(),
            userId = newUserId,
            provider = provider,
            subject = subject,
        )
        externalIdentityRepository.save(identity)

        return AuthResult(newUserId, isNewUser = true)
    }

    fun unbind(userId: UserId, provider: ExternalProvider) {
        val user = userRepository.findById(userId)
            ?: throw UserNotFoundException()

        val identities = externalIdentityRepository.findByUserId(userId)
        val hasPassword = user.hasPassword()
        val hasOtherExternal = identities.any { it.provider != provider }

        //TODO 将来增加有手机号/邮箱也可无密码
        require(hasPassword || hasOtherExternal) {
            "至少保留一种登录方式，无法解绑"
        }

        externalIdentityRepository.deleteByUserIdAndProvider(userId, provider)
    }
}

/**
 * 认证结果。
 */
data class AuthResult(
    val userId: UserId,
    val isNewUser: Boolean
)
