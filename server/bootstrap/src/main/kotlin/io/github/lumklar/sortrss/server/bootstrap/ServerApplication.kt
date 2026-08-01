package io.github.lumklar.sortrss.server.bootstrap

import io.github.lumklar.sortrss.server.adaptor.AdaptorAnchor
import io.github.lumklar.sortrss.server.application.ApplicationAnchor
import io.github.lumklar.sortrss.server.infrastructure.InfrastructureAnchor
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication(
    scanBasePackageClasses =
        [InfrastructureAnchor::class, ApplicationAnchor::class, AdaptorAnchor::class]
)
@EnableScheduling
class ServerApplication

fun main(args: Array<String>) {
    runApplication<ServerApplication>(*args)
}
