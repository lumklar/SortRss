package io.github.lumklar.sortrss.server.application.pojo.datasource.command

/**
 * 数据源连接配置命令（应用层专用）。
 * 使用密封类保证类型安全，每种数据源类型有独立的配置字段。
 */
sealed class DataSourceConnectionCommand {

    data class Fever(
        val endpoint: String,
        val username: String,
        val password: String
    ) : DataSourceConnectionCommand()

    data class GoogleReader(
        val endpoint: String,
        val accessToken: String
    ) : DataSourceConnectionCommand()
}

/**
 * 获取或创建数据源的命令。
 */
data class GetOrCreateDataSourceCommand(
    val connection: DataSourceConnectionCommand,
)