package io.github.lumklar.sortrss.server.infrastructure.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration  // 关键注解，必须有！
class SpringDocConfig {
    @Bean  // 手动创建OpenAPI Bean，3.0.x必须这么做
    fun openAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("SortRSS API文档")
                    .version("1.0")
                    .description("SortRSS API文档")
            )
    }
}

