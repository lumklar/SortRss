package io.github.lumklar.sortrss.server.bootstrap

import io.github.lumklar.sortrss.server.RootAnchor
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackageClasses = [RootAnchor::class])
class ServerApplication

fun main(args: Array<String>) {
    runApplication<ServerApplication>(*args)
}
