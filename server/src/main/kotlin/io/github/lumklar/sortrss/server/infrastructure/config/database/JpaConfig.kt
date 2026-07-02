package io.github.lumklar.sortrss.server.infrastructure.config

import io.github.lumklar.sortrss.server.infrastructure.config.DatabaseTypeDetector.isSQLite
import jakarta.persistence.EntityManagerFactory
import org.springframework.boot.jpa.EntityManagerFactoryBuilder
import org.springframework.boot.jpa.autoconfigure.JpaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import javax.sql.DataSource

@Configuration
class JpaConfig(
    private val dataSource: DataSource,
    private val jpaProperties: JpaProperties
) {

    @Bean
    fun entityManagerFactory(builder: EntityManagerFactoryBuilder): LocalContainerEntityManagerFactoryBean {
        val vendorAdapter = HibernateJpaVendorAdapter().apply {
            // 根据 DataSource URL 自动选择合适的方言？更稳妥的方式是手动判断
            // 但 Spring Boot 会自动根据 DataSource 推断方言，除非我们使用 SQLite 需要特殊指定
            // 在此我们手动设置方言（如果 DataSource 是 SQLite 则设置社区方言）
            if (dataSource.isSQLite()) {
                setDatabasePlatform("org.hibernate.dialect.SQLiteDialect") // 需要导入 hibernate-community-dialects
            }
            // 其他数据库如 PostgreSQL/MySQL 让 Hibernate 自动识别
            // 但也可以显式设置，不过自动识别更通用
        }

        val properties = jpaProperties.properties.toMutableMap()
        // 如果是 SQLite，可能需要额外配置（如 WAL 模式）
        if (dataSource.isSQLite()) {
            properties["hibernate.connection.url"] = dataSource.connection.metaData.url
            // 可选：开启 SQLite 的 WAL 模式
            properties["hibernate.connection.initSQL"] = "PRAGMA journal_mode=WAL;"
        }

        return builder
            .dataSource(dataSource)
            .packages("io.github.lumklar.sortrss.server.infrastructure.persistence.entity") // 替换为您的实体所在包
            .properties(properties)
            .jta(false)
            .build()
    }

    @Bean
    fun transactionManager(entityManagerFactory: EntityManagerFactory): JpaTransactionManager {
        return JpaTransactionManager(entityManagerFactory)
    }
}
