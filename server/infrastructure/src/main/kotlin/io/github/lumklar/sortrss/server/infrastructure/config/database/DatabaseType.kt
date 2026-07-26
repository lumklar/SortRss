package io.github.lumklar.sortrss.server.infrastructure.config;

/**
 * 数据库类型枚举
 */
enum class DatabaseType(val displayName: String) {
    SQLITE("SQLite (嵌入式)"),
    POSTGRESQL("PostgreSQL"),
    MYSQL("MySQL"),
    OTHER("其他数据库")
}
