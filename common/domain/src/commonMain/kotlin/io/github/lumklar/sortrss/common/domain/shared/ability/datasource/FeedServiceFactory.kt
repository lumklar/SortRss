package io.github.lumklar.sortrss.common.domain.shared.ability.datasource

import io.github.lumklar.sortrss.common.domain.model.datasource.DataSourceConnectionDetails

/**
 * 工厂：根据数据源连接详情创建对应的 FeedService 实例。
 * 领域层依赖此抽象，实现由基础设施层提供。
 */
interface FeedServiceFactory {
    /**
     * 创建对应数据源类型的 FeedService。
     * @throws IllegalArgumentException 如果连接详情类型与工厂支持的实现不匹配
     */
    fun create(connectionDetails: DataSourceConnectionDetails): FeedService
}