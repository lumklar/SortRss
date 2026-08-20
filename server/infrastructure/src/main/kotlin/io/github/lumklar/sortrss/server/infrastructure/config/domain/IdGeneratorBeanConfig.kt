package io.github.lumklar.sortrss.server.infrastructure.config.domain

import io.github.lumklar.sortrss.common.domain.model.folder.FolderMembershipId
import io.github.lumklar.sortrss.common.domain.model.user.ExternalIdentityId
import io.github.lumklar.sortrss.common.domain.model.user.UserId
import io.github.lumklar.sortrss.common.domain.shared.ability.IdGenerator
import io.github.lumklar.sortrss.common.foundation.shared.ability.id.ExternalIdentityIdGenerator
import io.github.lumklar.sortrss.common.foundation.shared.ability.id.FolderMembershipIdGenerator
import io.github.lumklar.sortrss.common.foundation.shared.ability.id.UserIdGenerator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class IdGeneratorBeanConfig {
    // 注册 FolderMembershipIdGenerator
    @Bean
    fun folderMembershipIdGenerator(): IdGenerator<FolderMembershipId> {
        return FolderMembershipIdGenerator()
    }

    // 注册 UserIdGenerator
    @Bean
    fun userIdGenerator(): IdGenerator<UserId> {
        return UserIdGenerator()
    }

    // 注册 ExternalIdentityIdGenerator
    @Bean
    fun externalIdentityIdGenerator(): IdGenerator<ExternalIdentityId> {
        return ExternalIdentityIdGenerator()
    }
}