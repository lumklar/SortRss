package io.github.lumklar.sortrss.server.infrastructure.config.domain

import io.github.lumklar.sortrss.common.domain.model.datasource.DataSourceId
import io.github.lumklar.sortrss.common.domain.model.datasource.DataSourceRepository
import io.github.lumklar.sortrss.common.domain.model.feed.FeedRepository
import io.github.lumklar.sortrss.common.domain.model.folder.FolderMembershipId
import io.github.lumklar.sortrss.common.domain.model.folder.FolderMembershipRepository
import io.github.lumklar.sortrss.common.domain.model.folder.FolderRepository
import io.github.lumklar.sortrss.common.domain.model.user.ExternalIdentityId
import io.github.lumklar.sortrss.common.domain.model.user.ExternalIdentityRepository
import io.github.lumklar.sortrss.common.domain.model.user.UserId
import io.github.lumklar.sortrss.common.domain.model.user.UserRepository
import io.github.lumklar.sortrss.common.domain.service.DataSourceAccessService
import io.github.lumklar.sortrss.common.domain.service.DataSourceManagementService
import io.github.lumklar.sortrss.common.domain.service.ExternalAccountService
import io.github.lumklar.sortrss.common.domain.service.FolderMembershipService
import io.github.lumklar.sortrss.common.domain.shared.ability.IdGenerator
import io.github.lumklar.sortrss.common.domain.shared.ability.datasource.FeedServiceFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DomainServiceBeanConfig {

    // 已有的外部账户服务 Bean
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

    // 已有的文件夹成员关系服务 Bean
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

    // 数据源管理服务 Bean
    @Bean
    fun dataSourceManagementService(
        feedServiceFactory: FeedServiceFactory,
        dataSourceRepository: DataSourceRepository,
        userRepository: UserRepository,
        idGenerator: IdGenerator<DataSourceId>
    ): DataSourceManagementService {
        return DataSourceManagementService(
            feedServiceFactory,
            dataSourceRepository,
            userRepository,
            idGenerator
        )
    }

    // 数据源访问服务 Bean（依赖上面的 dataSourceManagementService）
    @Bean
    fun dataSourceAccessService(
        userIdGenerator: IdGenerator<UserId>,
        userRepository: UserRepository,
        dataSourceRepository: DataSourceRepository,
        dataSourceManagementService: DataSourceManagementService
    ): DataSourceAccessService {
        return DataSourceAccessService(
            userIdGenerator,
            userRepository,
            dataSourceRepository,
            dataSourceManagementService
        )
    }
}
