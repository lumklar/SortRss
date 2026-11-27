package io.github.lumklar.sortrss.server.infrastructure.config.database

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.database")
class AppDatabaseProperties {
    /**
     * Spring Boot AOT 会为 @ConfigurationProperties 的嵌套类型自动注册字段与方法反射提示，
     * native 镜像里 SqliteProperties.path 的读写不依赖 @NestedConfigurationProperty，
     * 因此不标注该注解（它只影响 IDE 的配置元数据提示层级）。
     */
    var sqlite: SqliteProperties = SqliteProperties()
}

class SqliteProperties {
    var path: String = "./rss.db"
}
