package io.github.lumklar.sortrss.server.adaptor.web.config

import io.github.lumklar.sortrss.common.api.annotation.ApiRoute
import io.github.lumklar.sortrss.common.api.annotation.HttpMethod
import io.github.lumklar.sortrss.common.api.dto.ApiExtra
import io.github.lumklar.sortrss.common.api.dto.ApiResult
import io.github.lumklar.sortrss.common.api.dto.ErrorSource
import io.github.lumklar.sortrss.common.api.service.FeedApi
import io.github.lumklar.sortrss.common.api.service.UserApi
import org.springframework.aot.hint.MemberCategory
import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.RuntimeHintsRegistrar
import org.springframework.aot.hint.TypeReference

/**
 * [ApiRouteAutoRegister] 在启动期通过反射读取接口方法上的 @ApiRoute。
 * AOT 只会注册实现类的方法，接口方法/注解需要显式补注册，否则 native 下
 * 基于 @ApiRoute 的路由不会被注册。
 */
class WebNativeHints : RuntimeHintsRegistrar {
    override fun registerHints(hints: RuntimeHints, classLoader: ClassLoader?) {
        hints.reflection().registerType(FeedApi::class.java, MemberCategory.INVOKE_DECLARED_METHODS)
        hints.reflection().registerType(UserApi::class.java, MemberCategory.INVOKE_DECLARED_METHODS)
        hints.reflection().registerType(ApiRoute::class.java, MemberCategory.INVOKE_DECLARED_METHODS)
        hints.reflection().registerType(HttpMethod::class.java, MemberCategory.INVOKE_DECLARED_METHODS)

        // AOT 只注册了 ApiResult 外部类；运行时实际序列化的是 Success/Failure 子类，
        // 不补注册的话 native 下 Jackson 会得到空对象 "{}"。
        listOf(
            ApiResult::class.java,
            ApiResult.Success::class.java,
            ApiResult.Failure::class.java,
            ApiExtra::class.java,
            ErrorSource::class.java
        ).forEach {
            hints.reflection().registerType(
                it,
                MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                MemberCategory.INVOKE_DECLARED_METHODS,
                MemberCategory.ACCESS_DECLARED_FIELDS
            )
        }

        // springdoc 3.2.1 在生成 OpenAPI 时会走 Kotlin 反射读取注解元数据，
        // Kotlin/JDK 会把注解实例化为 JDK 动态代理。native 下未提前声明，
        // /v3/api-docs 会抛 MissingReflectionRegistrationError（500）。
        hints.proxies().registerJdkProxy(
            TypeReference.of("kotlin.internal.IntrinsicConstEvaluation")
        )

        // springdoc 通过 therapi-runtime-javadoc 读取 KAPT 生成的 API 注释，
        // 这些元数据以 <包名>/<类名>__Javadoc.json 资源形式打包在依赖 jar 中。
        // native 默认不会把未显式声明的资源打进镜像，导致 /v3/api-docs 丢失
        // 接口/字段/参数的 summary、description、@param、@return 等文档。
        hints.resources().registerPattern("**/*__Javadoc.json")
    }
}
