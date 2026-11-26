package io.github.lumklar.sortrss.server.adaptor.web.config

import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.ImportRuntimeHints
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping

/**
 * Controller 出入参由 Spring AOT 自动注册；@ApiRoute 接口的反射提示见 [WebNativeHints]。
 */
@Configuration
@ImportRuntimeHints(WebNativeHints::class)
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