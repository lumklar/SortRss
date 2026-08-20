package io.github.lumklar.sortrss.server.infrastructure.config.domain

import io.github.lumklar.sortrss.common.domain.model.feed.FeedRepository
import io.github.lumklar.sortrss.common.domain.model.folder.FolderMembershipId
import io.github.lumklar.sortrss.common.domain.model.folder.FolderMembershipRepository
import io.github.lumklar.sortrss.common.domain.model.folder.FolderRepository
import io.github.lumklar.sortrss.common.domain.model.user.ExternalIdentityId
import io.github.lumklar.sortrss.common.domain.model.user.ExternalIdentityRepository
import io.github.lumklar.sortrss.common.domain.model.user.UserId
import io.github.lumklar.sortrss.common.domain.model.user.UserRepository
import io.github.lumklar.sortrss.common.domain.service.ExternalAccountService
import io.github.lumklar.sortrss.common.domain.service.FolderMembershipService
import io.github.lumklar.sortrss.common.domain.shared.ability.IdGenerator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DomainServiceBeanConfig {

    // 注入外部账户服务
    @Bean
    fun externalAccountService(
        userIdGenerator: IdGenerator<UserId>,
        externalIdGenerator: IdGenerator<ExternalIdentityId>,
        userRepository: UserRepository,
        externalIdentityRepository: ExternalIdentityRepository
    ): ExternalAccountService {
        return ExternalAccountService(
            userIdGenerator,
            externalIdGenerator,
            userRepository,
            externalIdentityRepository
        )
    }

    // 注入文件夹成员关系服务
    @Bean
    fun folderMembershipService(
        membershipIdGenerator: IdGenerator<FolderMembershipId>,
        membershipRepo: FolderMembershipRepository,
        folderRepo: FolderRepository,
        feedRepo: FeedRepository
    ): FolderMembershipService {
        return FolderMembershipService(
            membershipIdGenerator,
            membershipRepo,
            folderRepo,
            feedRepo
        )
    }
}
