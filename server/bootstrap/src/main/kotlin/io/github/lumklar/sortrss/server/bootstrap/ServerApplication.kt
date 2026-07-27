package io.github.lumklar.sortrss.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["io.github.lumklar.sortrss.server"] )
class ServerApplication

fun main(args: Array<String>) {
    runApplication<ServerApplication>(*args)
}
