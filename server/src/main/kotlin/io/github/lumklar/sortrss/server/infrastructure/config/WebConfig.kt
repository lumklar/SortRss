package io.github.lumklar.sortrss.server.infrastructure.config

import io.github.lumklar.sortrss.common.api.annotation.ApiRoute
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping

/**
 * TODO native支持
 */
@Configuration
class WebConfig {

    /**
     * 注册自定义路由自动注册器
     * 自动扫描并注册 [ApiRoute] 注解的路由配置
     * @see ApiRoute
     */
    @Bean
    fun apiRouteAutoRegister(
        context: ApplicationContext,
        requestMappingHandlerMapping: RequestMappingHandlerMapping
    ): ApiRouteAutoRegister {
        return ApiRouteAutoRegister(context, requestMappingHandlerMapping)
    }
}
