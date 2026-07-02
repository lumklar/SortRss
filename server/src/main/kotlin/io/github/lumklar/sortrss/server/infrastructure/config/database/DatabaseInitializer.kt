package io.github.lumklar.sortrss.server.infrastructure.config

import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import java.io.File
import javax.sql.DataSource

@Component
class DatabaseInitializer(
    private val dataSource: DataSource,
    private val appDatabaseProperties: AppDatabaseProperties  // 用于获取 SQLite 文件路径
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @EventListener(ApplicationReadyEvent::class)
    fun logDatabaseInfo() {
        try {
            val url = dataSource.connection.metaData.url
            val dbType = DatabaseTypeDetector.detect(url)

            log.info("===============================================")
            log.info("当前数据库类型：${dbType.displayName}")
            log.info("连接地址：$url")

            when (dbType) {
                DatabaseType.SQLITE -> {
                    val dbPath = File(appDatabaseProperties.sqlite.path).absolutePath
                    log.info("数据文件位置：$dbPath")
                    log.info("")
                    log.info("▶ 当前使用嵌入式 SQLite 数据库（默认兜底模式）")
                    log.info("▶ 如需切换至 PostgreSQL / MySQL 等外部数据库，请设置以下环境变量：")
                    log.info("    DB_URL=jdbc:postgresql://localhost:5432/yourdb")
                    log.info("    DB_USER=your_username")
                    log.info("    DB_PASSWORD=your_password")
                    log.info("    DB_DRIVER=org.postgresql.Driver   # 或 com.mysql.cj.jdbc.Driver")
                    log.info("")
                    log.info("▶ 若已设置上述变量但此处仍显示 SQLite，请检查配置是否正确加载。")
                }
                else -> {
                    log.info("")
                    log.info("▶ 当前使用外部数据库，配置来源于环境变量（DB_URL / DB_USER / DB_PASSWORD / DB_DRIVER）")
                    log.info("▶ 如需调整连接信息，请修改对应的环境变量并重启应用。")
                }
            }
            log.info("===============================================")

        } catch (e: Exception) {
            log.warn("无法获取数据库信息（可能连接未就绪），请忽略此警告。", e)
        }
    }
}