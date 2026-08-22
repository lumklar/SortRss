package io.github.lumklar.sortrss.server.application.service

import io.github.lumklar.sortrss.common.domain.model.datasource.DataSourceConnectionDetails
import io.github.lumklar.sortrss.server.application.pojo.datasource.command.DataSourceConnectionCommand

/**
 * 数据源连接详情工厂（应用层定义接口，基础设施层实现）。
 * 用于将应用层命令转换为领域层可理解的连接详情对象。
 */
interface DataSourceConnectionFactory {
    fun create(command: DataSourceConnectionCommand): DataSourceConnectionDetails
}
