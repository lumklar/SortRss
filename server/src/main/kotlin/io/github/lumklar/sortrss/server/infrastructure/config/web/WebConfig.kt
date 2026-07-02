package io.github.lumklar.sortrss.server.infrastructure.config.web

import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping

@Configuration
class WebConfig {

    /**
     * 注册自定义路由自动注册器
     */
    @Bean
    fun apiRouteAutoRegister(
        context: ApplicationContext,
        requestMappingHandlerMapping: RequestMappingHandlerMapping
    ): ApiRouteAutoRegister {
        return ApiRouteAutoRegister(context, requestMappingHandlerMapping)
    }
}