package io.github.lumklar.sortrss.server.infrastructure.config.database

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.NestedConfigurationProperty

@ConfigurationProperties(prefix = "app.database")
class AppDatabaseProperties {
    /**
     * TODO native下一定要注解NestedConfigurationProperty？
     */
    @NestedConfigurationProperty
    var sqlite: SqliteProperties = SqliteProperties()
}

class SqliteProperties {
    var path: String = "./rss.db"
}
