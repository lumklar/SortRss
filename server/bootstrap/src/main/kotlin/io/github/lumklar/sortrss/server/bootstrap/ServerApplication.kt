package io.github.lumklar.sortrss.server.bootstrap

import io.github.lumklar.sortrss.server.adaptor.AdaptorAnchor
import io.github.lumklar.sortrss.server.application.ApplicationAnchor
import io.github.lumklar.sortrss.server.infrastructure.InfrastructureAnchor
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ImportRuntimeHints
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication(
    scanBasePackageClasses =
        [InfrastructureAnchor::class, ApplicationAnchor::class, AdaptorAnchor::class]
)
@EnableScheduling
@ImportRuntimeHints(LoggingNativeHints::class)
class ServerApplication

fun main(args: Array<String>) {
    // Spring Boot 4.2 的 spring-core 改用真实 commons-logging 1.4，默认 Log4jApiLogFactory
    // 会经 log4j-to-slf4j 在启动极早期触碰 org.slf4j.MarkerFactory。native 下此时 SLF4J
    // Provider 尚未绑定，导致 "Could not initialize class org.slf4j.MarkerFactory"。
    // 强制使用 commons-logging 自带的 SLF4J 直连工厂，绕开 log4j 桥接（JVM 下无副作用）。
    if (System.getProperty("org.apache.commons.logging.LogFactory") == null) {
        System.setProperty(
            "org.apache.commons.logging.LogFactory",
            "org.apache.commons.logging.impl.Slf4jLogFactory"
        )
    }
    runApplication<ServerApplication>(*args)
}
