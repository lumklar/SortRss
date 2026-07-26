package io.github.lumklar.sortrss.server.infrastructure.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "app.database")
class AppDatabaseProperties {
    lateinit var sqlite: SqliteProperties
}

class SqliteProperties {
    lateinit var path: String
}
