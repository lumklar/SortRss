package io.github.lumklar.sortrss.server.adaptor.web.config

import io.github.lumklar.sortrss.common.api.annotation.ApiRoute
import io.github.lumklar.sortrss.common.api.annotation.HttpMethod
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.core.annotation.AnnotatedElementUtils
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import java.lang.reflect.Method

/**
 * 把接口方法上的 [ApiRoute] 当作原生 @RequestMapping 来用。
 *
 * 关键点：覆写 [getMappingForMethod] 让路由参与 Spring 最初的 handler 扫描，
 * 而不是在容器启动完成后调用 `registerMapping` 补注册。后补注册的映射虽然
 * 能被 DispatcherServlet 命中，但 springdoc 在容器初始化阶段就缓存了 handler
 * 快照，OpenAPI 文档会缺少这些接口；启动期才注册也会留下一段「端口已开、
 * 路由未就绪」的窗口。
 *
 * 实现方法上已存在 @RequestMapping 时，保留原生注解并校验两者一致，
 * 避免同一个接口出现两份会漂移的配置。
 */
class ApiRouteRequestMappingHandlerMapping : RequestMappingHandlerMapping() {

    private val routeLogger = KotlinLogging.logger {}

    override fun getMappingForMethod(method: Method, handlerType: Class<*>): RequestMappingInfo? {
        // 父类会把方法级与方法所在的类级 @RequestMapping 合并，得到实际生效的映射
        val nativeMapping = super.getMappingForMethod(method, handlerType)
        val apiRoute = findApiRoute(method, handlerType) ?: return nativeMapping

        if (nativeMapping != null) {
            verifyConsistency(method, apiRoute, nativeMapping)
            routeLogger.info {
                "Route declared by RequestMapping annotation: ${apiRoute.method} ${apiRoute.value}"
            }
            return nativeMapping
        }

        val apiMapping = buildMapping(apiRoute)
        // 类级 @RequestMapping 是路径前缀，需要与 @ApiRoute 声明的路径合并
        val typeMapping = buildTypeMapping(handlerType)
        val mapping = typeMapping?.combine(apiMapping) ?: apiMapping
        routeLogger.info { "Route declared by ApiRoute annotation: ${apiRoute.method} ${apiRoute.value}" }
        return mapping
    }

    /**
     * 在控制器实现的接口方法上查找 [ApiRoute]。
     */
    private fun findApiRoute(method: Method, handlerType: Class<*>): ApiRoute? {
        for (apiInterface in handlerType.interfaces) {
            val interfaceMethod = runCatching {
                apiInterface.getMethod(method.name, *method.parameterTypes)
            }.getOrNull() ?: continue
            // 显式声明类型：Kotlin 2.4 对 getAnnotation 推断出的平台类型会尝试加载
            // 不可访问的 @UnknownNullability 类型注解，交给 let 推断会触发编译错误
            val apiRoute: ApiRoute? = interfaceMethod.getAnnotation(ApiRoute::class.java)
            if (apiRoute != null) return apiRoute
        }
        return null
    }

    /**
     * 由 [ApiRoute] 构建映射，必须沿用 handler mapping 自身的 [builderConfiguration]，
     * 否则会退化成 Ant 版路径条件，与 Spring 7 默认的 PathPatternParser 不一致。
     */
    private fun buildMapping(apiRoute: ApiRoute): RequestMappingInfo {
        return RequestMappingInfo
            .paths(apiRoute.value)
            .methods(convertMethod(apiRoute.method))
            .options(builderConfiguration)
            .build()
    }

    /**
     * 构建控制器类级 @RequestMapping 前缀映射；未声明时返回 null。
     */
    private fun buildTypeMapping(handlerType: Class<*>): RequestMappingInfo? {
        val typeRequestMapping = AnnotatedElementUtils
            .findMergedAnnotation(handlerType, RequestMapping::class.java)
            ?: return null
        if (typeRequestMapping.value.isEmpty()) return null
        return RequestMappingInfo
            .paths(*typeRequestMapping.value)
            .options(builderConfiguration)
            .build()
    }

    /**
     * 校验 @ApiRoute 与实现方法上的 @RequestMapping 是否描述同一个接口。
     * 传入的 [nativeMapping] 已包含类级前缀，因此类级 + 方法级的写法不会被误判。
     *
     * @throws IllegalStateException 两处声明不一致时抛出，启动即失败
     */
    private fun verifyConsistency(method: Method, apiRoute: ApiRoute, nativeMapping: RequestMappingInfo) {
        val paths = nativeMapping.patternValues
        val methods = nativeMapping.methodsCondition.methods
        val pathMatch = apiRoute.value in paths
        // 未限定 HTTP 方法时 Spring 会映射所有方法，视为一致
        val methodMatch = methods.isEmpty() || convertMethod(apiRoute.method) in methods
        if (pathMatch && methodMatch) return

        val errorMsg = buildString {
            append("❌ 路由配置冲突：方法 ${method.declaringClass.simpleName}.${method.name}")
            append("\n@ApiRoute 配置：${apiRoute.method} ${apiRoute.value}")
            append(
                "\n@RequestMapping 实际生效配置：" +
                    "${methods.ifEmpty { setOf("不限定，映射所有方法") }} ${paths.ifEmpty { setOf("(空路径)") }}"
            )
            append("\n请保证路径和HTTP方法完全一致，或删除其中一个注解！")
        }
        throw IllegalStateException(errorMsg)
    }

    private fun convertMethod(method: HttpMethod): RequestMethod {
        return when (method) {
            HttpMethod.GET -> RequestMethod.GET
            HttpMethod.POST -> RequestMethod.POST
            HttpMethod.PUT -> RequestMethod.PUT
            HttpMethod.DELETE -> RequestMethod.DELETE
            HttpMethod.PATCH -> RequestMethod.PATCH
            HttpMethod.HEAD -> RequestMethod.HEAD
            HttpMethod.OPTIONS -> RequestMethod.OPTIONS
            HttpMethod.TRACE -> RequestMethod.TRACE
        }
    }
}
