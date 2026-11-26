package io.github.lumklar.sortrss.server.bootstrap

import org.springframework.aot.hint.MemberCategory
import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.RuntimeHintsRegistrar

/**
 * Spring Framework 7.1（Spring Boot 4.2）改用真实 commons-logging 1.4，
 * 其 Log4jApiLogFactory 会经 log4j-to-slf4j 先触碰 org.slf4j.MarkerFactory。
 * 此时 LoggerFactory 尚未被加载，slf4j-api 中「以 LoggerFactory 为条件」的
 * META-INF/services/org.slf4j.spi.SLF4JServiceProvider 资源提示不会生效，
 * native 下 SLF4J 找不到 Provider → MarkerFactory 静态初始化失败
 * （NoClassDefFoundError: Could not initialize class org.slf4j.MarkerFactory）。
 *
 * 这里无条件注册 SLF4J ServiceProvider 资源与实现类的反射，避免加载顺序依赖。
 */
class LoggingNativeHints : RuntimeHintsRegistrar {
    override fun registerHints(hints: RuntimeHints, classLoader: ClassLoader?) {
        hints.resources().registerPattern("META-INF/services/org.slf4j.spi.SLF4JServiceProvider")

        listOf(
            "org.slf4j.spi.SLF4JServiceProvider",
            "ch.qos.logback.classic.spi.LogbackServiceProvider",
            // logback 1.6 在 native 下找不到 logback-spring.xml（由 Spring 自行初始化），
            // Provider 初始化时回退到 BasicConfigurator，需注册其无参构造反射。
            "ch.qos.logback.classic.BasicConfigurator"
        ).forEach { name ->
            hints.reflection().registerType(
                org.springframework.aot.hint.TypeReference.of(name),
                MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                MemberCategory.INVOKE_DECLARED_METHODS,
                MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
                MemberCategory.INVOKE_PUBLIC_METHODS
            )
        }
    }
}
