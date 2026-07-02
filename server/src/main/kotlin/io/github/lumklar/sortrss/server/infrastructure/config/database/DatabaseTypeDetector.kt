package io.github.lumklar.sortrss.server.infrastructure.config

import javax.sql.DataSource

/**
 * 数据库类型检测工具
 */
object DatabaseTypeDetector {

    /**
     * 根据 JDBC URL 判断数据库类型
     */
    fun detect(url: String): DatabaseType {
        return when {
            url.contains("sqlite", ignoreCase = true) -> DatabaseType.SQLITE
            url.contains("postgresql", ignoreCase = true) -> DatabaseType.POSTGRESQL
            url.contains("mysql", ignoreCase = true) -> DatabaseType.MYSQL
            else -> DatabaseType.OTHER
        }
    }

    /**
     * 根据 DataSource 判断数据库类型（安全获取 URL，异常时返回 OTHER）
     */
    fun detect(dataSource: DataSource): DatabaseType {
        return try {
            dataSource.connection.use { conn ->
                detect(conn.metaData.url)
            }
        } catch (e: Exception) {
            DatabaseType.OTHER
        }
    }

    // 便捷判断方法
    fun isSQLite(dataSource: DataSource): Boolean = detect(dataSource) == DatabaseType.SQLITE
    fun isPostgreSQL(dataSource: DataSource): Boolean = detect(dataSource) == DatabaseType.POSTGRESQL
    fun isMySQL(dataSource: DataSource): Boolean = detect(dataSource) == DatabaseType.MYSQL

    /**
     * DataSource 扩展：获取数据库类型
     */
    fun DataSource.databaseType(): DatabaseType = DatabaseTypeDetector.detect(this)

    /**
     * DataSource 扩展：判断是否为 SQLite
     */
    fun DataSource.isSQLite(): Boolean = DatabaseTypeDetector.isSQLite(this)

    // 其他类型同理
    fun DataSource.isPostgreSQL(): Boolean = DatabaseTypeDetector.isPostgreSQL(this)
    fun DataSource.isMySQL(): Boolean = DatabaseTypeDetector.isMySQL(this)
}
