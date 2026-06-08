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
        // 获取所有 @RestController
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

                    checkMethodConflict(targetMethod)

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

    private fun checkMethodConflict(method: Method) {
        // 会自动识别 @RequestMapping + 所有组合注解（GetMapping/PostMapping等）
        if (AnnotatedElementUtils.hasAnnotation(method, RequestMapping::class.java)) {
            throw IllegalStateException(
                "❌ 冲突禁止：方法 ${method.declaringClass.simpleName}.${method.name} " +
                        "已使用 @ApiRoute，不能同时使用 @RequestMapping/@GetMapping/@PostMapping 等注解！"
            )
        }
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
