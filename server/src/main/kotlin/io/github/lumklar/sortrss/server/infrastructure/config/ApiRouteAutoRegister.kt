package io.github.lumklar.sortrss.server.infrastructure.config

import io.github.lumklar.sortrss.common.api.annotation.ApiRoute
import io.github.lumklar.sortrss.common.api.annotation.HttpMethod
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.boot.CommandLineRunner
import org.springframework.context.ApplicationContext
import org.springframework.core.annotation.AnnotatedElementUtils
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import java.lang.reflect.Method

/**
 * 自动扫描并注册 [ApiRoute] 注解的路由配置
 * @see ApiRoute
 */
class ApiRouteAutoRegister(
    private val applicationContext: ApplicationContext,
    // 注入 Spring 原生的路由注册器（核心！）
    private val requestMappingHandlerMapping: RequestMappingHandlerMapping
) : CommandLineRunner {

    private val logger = KotlinLogging.logger {}

    override fun run(vararg args: String) {
        registerCustomRoutes()
    }

    private fun registerCustomRoutes() {
        // 获取所有 @Controller
        val controllers = applicationContext.getBeansWithAnnotation(Controller::class.java).values

        for (controller in controllers) {
            val controllerClass = controller::class.java

            // 遍历实现的接口
            for (apiInterface in controllerClass.interfaces) {
                for (interfaceMethod in apiInterface.declaredMethods) {
                    // 获取 @ApiRoute 注解
                    val apiRoute = interfaceMethod.getAnnotation(ApiRoute::class.java) ?: continue

                    // 找到实现方法
                    val targetMethod = findMethod(controllerClass, interfaceMethod) ?: continue

                    checkMethodConflict(targetMethod, apiRoute)

                    // 构建路由
                    val mapping = RequestMappingInfo
                        .paths(apiRoute.value)
                        .methods(convertMethod(apiRoute.method))
                        .build()
                    //注册路由
                    requestMappingHandlerMapping.registerMapping(mapping, controller, targetMethod)

                    logger.info { "✅ 注册路由成功：${apiRoute.method} ${apiRoute.value}" }
                }
            }
        }
    }

    /**
     * 校验路由冲突
     * @return true=需要注册ApiRoute；false=已存在一致配置，跳过注册
     * @throws IllegalStateException 路由配置不一致时抛出异常
     */
    private fun checkMethodConflict(method: Method, apiRoute: ApiRoute): Boolean {
        // 获取方法上 合并后的 @RequestMapping 注解（支持@GetMapping/@PostMapping等所有组合注解）
        val requestMapping = AnnotatedElementUtils.findMergedAnnotation(method, RequestMapping::class.java)
            ?: return true // 无原生RequestMapping注解，直接允许注册

        // ========== 提取原生注解的配置 ==========
        // 1. 路径（value/path 是别名，统一取value）
        val rmPaths = requestMapping.value.toSet()
        val apiPath = apiRoute.value

        // 2. HTTP请求方法
        val rmMethods = requestMapping.method.toSet()
        val apiMethod = convertMethod(apiRoute.method)

        // ========== 对比配置是否一致 ==========
        //FIXME 需要同时判断和controller的requestmapping是否一致
        val pathMatch = apiPath in rmPaths
        val methodMatch = apiMethod in rmMethods

        // 两个配置都一致：跳过注册
        if (pathMatch && methodMatch) {
            return false
        }

        // 任意配置不一致：抛出异常
        val errorMsg = buildString {
            append("❌ 路由配置冲突：方法 ${method.declaringClass.simpleName}.${method.name}")
            append("\n@ApiRoute 配置：${apiRoute.method} ${apiRoute.value}")
            append("\n@RequestMapping 配置：${rmMethods.takeIf { it.isNotEmpty() } ?: "[默认所有方法]"} ${rmPaths}")
            append("\n请保证路径和HTTP方法完全一致，或删除其中一个注解！")
        }
        throw IllegalStateException(errorMsg)
    }

    // 匹配方法
    private fun findMethod(controllerClass: Class<*>, method: Method): Method? {
        return runCatching {
            controllerClass.getMethod(method.name, *method.parameterTypes)
        }.getOrNull()
    }

    // 转换HTTP方法
    private fun convertMethod(method: HttpMethod): org.springframework.web.bind.annotation.RequestMethod {
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
