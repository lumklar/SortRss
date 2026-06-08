package io.github.lumklar.sortrss.server.infrastructure.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
 * API 响应配置
 * 配置是否暴露内部扩展信息 ApiExtra
 */
@Component
@ConfigurationProperties(prefix = "api.response")
class ApiResponseProperties {
    /**
     * 是否排除 ApiExtra 扩展信息（true=隐藏，生产环境使用；false=显示，开发环境使用）
     */
    var excludeExtra: Boolean = true
}

