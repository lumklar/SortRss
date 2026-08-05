package io.github.lumklar.sortrss.server.infrastructure.config.database

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.database")
class AppDatabaseProperties {
    var sqlite: SqliteProperties = SqliteProperties()
}

class SqliteProperties {
    var path: String = "./rss.db"
}
