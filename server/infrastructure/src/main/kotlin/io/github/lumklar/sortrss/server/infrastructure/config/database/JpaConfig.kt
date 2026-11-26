package io.github.lumklar.sortrss.server.infrastructure.config.database

import io.github.lumklar.sortrss.server.infrastructure.config.database.DatabaseTypeDetector.isSQLite
import io.github.lumklar.sortrss.server.infrastructure.persistence.PersistenceAnchor
import org.springframework.boot.hibernate.autoconfigure.HibernatePropertiesCustomizer
import org.springframework.boot.persistence.autoconfigure.EntityScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.ImportRuntimeHints
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import javax.sql.DataSource

@Configuration
@EntityScan(basePackageClasses = [PersistenceAnchor::class])
@EnableJpaRepositories(basePackageClasses = [PersistenceAnchor::class])
@ImportRuntimeHints(DatabaseNativeHints::class)
class JpaConfig(
    private val dataSource: DataSource
) {

    /**
     * 不能用自定义 EntityManagerFactory Bean：native 下它会绕过 AOT 生成的
     * 持久化单元元数据，导致所有 @Entity 都变成 "Not a managed type"。
     * 方言改为通过 HibernatePropertiesCustomizer 注入，保留 Boot 自动配置的 EMF。
     */
    @Bean
    fun sqliteHibernatePropertiesCustomizer(): HibernatePropertiesCustomizer =
        HibernatePropertiesCustomizer { properties ->
            if (dataSource.isSQLite()) {
                // 方言来自 hibernate-community-dialects（runtimeOnly），
                // 这里用类名字符串并在 DatabaseNativeHints 中注册反射。
                properties["hibernate.dialect"] = "org.hibernate.community.dialect.SQLiteDialect"
                properties["hibernate.connection.initSQL"] = "PRAGMA journal_mode=WAL;"
            }
        }
}
