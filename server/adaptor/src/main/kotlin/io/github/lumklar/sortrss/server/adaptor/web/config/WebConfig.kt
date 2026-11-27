package io.github.lumklar.sortrss.server.adaptor.web.config

import org.springframework.boot.webmvc.autoconfigure.WebMvcRegistrations
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.ImportRuntimeHints
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping

/**
 * Controller 出入参由 Spring AOT 自动注册；@ApiRoute 接口的反射提示见 [WebNativeHints]。
 */
@Configuration
@ImportRuntimeHints(WebNativeHints::class)
class WebConfig : WebMvcRegistrations {

    /**
     * 替换默认的 RequestMappingHandlerMapping，使 @ApiRoute 在 handler 扫描阶段
     * 就与 @RequestMapping 等价地参与映射构建。
     */
    override fun getRequestMappingHandlerMapping(): RequestMappingHandlerMapping {
        return ApiRouteRequestMappingHandlerMapping()
    }
}
