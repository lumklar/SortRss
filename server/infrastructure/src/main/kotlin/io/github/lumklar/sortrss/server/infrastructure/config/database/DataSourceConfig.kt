package io.github.lumklar.sortrss.server.infrastructure.config

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.File
import javax.sql.DataSource

@Configuration
class DataSourceConfig(
    private val appDatabaseProperties: AppDatabaseProperties,  // 自动注入
) {
    @Bean
    @ConditionalOnExpression("'\${spring.datasource.url:}' == ''")
    fun sqliteDataSource(): DataSource {
        val dbPath = appDatabaseProperties.sqlite.path
        val dbFile = File(dbPath)
        // 确保父目录存在
        dbFile.parentFile?.mkdirs()

        val url = "jdbc:sqlite:${dbFile.absolutePath}"
        return DataSourceBuilder.create()
            .url(url)
            .driverClassName("org.sqlite.JDBC")
            .build()
    }
}