package io.github.lumklar.sortrss.server.infrastructure.config.domain

import io.github.lumklar.sortrss.common.domain.model.user.PasswordPolicy
import io.github.lumklar.sortrss.common.domain.model.user.StandardPasswordPolicy
import io.github.lumklar.sortrss.common.domain.shared.ability.PasswordEncoder
import io.github.lumklar.sortrss.common.domain.shared.ability.datasource.FeedServiceFactory
import io.github.lumklar.sortrss.common.foundation.shared.ability.datasource.DefaultFeedServiceFactory
import io.github.lumklar.sortrss.server.infrastructure.security.BCryptPasswordEncoder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DomainAbilityBeanConfig {
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun passwordPolicy(): PasswordPolicy {
        return StandardPasswordPolicy()
    }

    @Bean
    fun feedServiceFactory(): FeedServiceFactory {
        return DefaultFeedServiceFactory()
    }
}
