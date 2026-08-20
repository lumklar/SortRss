package io.github.lumklar.sortrss.common.domain.model.user

import kotlin.uuid.Uuid

/**
 * 第三方身份映射表（值对象）
 * 仅维护 “外部提供方 + 外部用户标识” 与 “系统内部用户ID” 的绑定关系
 */
class ExternalIdentity private constructor(
    val id: ExternalIdentityId,
    val userId: UserId,
    val provider: ExternalProvider,
    val subject: String   // 第三方平台唯一用户标识
) {
    companion object {
        // 工厂方法：创建新绑定
        fun create(
            id: ExternalIdentityId,
            userId: UserId,
            provider: ExternalProvider,
            subject: String
        ): ExternalIdentity {
            require(subject.isNotBlank()) { "subject 不能为空" }
            return ExternalIdentity(id, userId, provider, subject)
        }

        // 工厂方法：从持久化层重建（注意枚举解析的安全性提升）
        fun reconstruct(
            id: Uuid,
            userId: Uuid,
            provider: ExternalProvider,
            subject: String
        ): ExternalIdentity {
            return ExternalIdentity(
                ExternalIdentityId(id),
                UserId(userId),
                provider,
                subject
            )
        }
    }
}