package io.github.lumklar.sortrss.server.infrastructure.config

import io.github.lumklar.sortrss.common.domain.shared.ability.PasswordEncoder
import io.github.lumklar.sortrss.common.domain.service.UserDomainService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DomainServiceBeanConfig {
    @Bean
    fun userDomainService(passwordEncoder: PasswordEncoder): UserDomainService {
        return UserDomainService(passwordEncoder);
    }
}