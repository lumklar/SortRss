package io.github.lumklar.sortrss.server.infrastructure.config

import io.github.lumklar.sortrss.common.domain.model.ability.PasswordEncoder
import io.github.lumklar.sortrss.server.infrastructure.security.BCryptPasswordEncoder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DomainAbilityBeanConfig {
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}
