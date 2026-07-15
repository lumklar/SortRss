package io.github.lumklar.sortrss.common.domain.model.entity

/**
 * 数据来源类型。
 * - LOCAL_OPML: 用户通过 OPML 文件导入的本地订阅。
 * - FEVER_API: 兼容 Fever API 的远程数据源。
 * - GOOGLE_READER_API: 兼容 Google Reader API 的远程数据源。
 */
enum class DataSourceType {
    /**
     *本地订阅
     */
    LOCAL_OPML,

    /**
     * Fever API
     */
    FEVER_API,

    /**
     * Google Reader 兼容 API
     */
    GOOGLE_READER_API;

    /** 是否为远程数据源（非本地 OPML） */
    fun isRemote(): Boolean = this != LOCAL_OPML

    /** 是否支持本地刷新（即本地缓存） */
    fun supportLocalRefresh(): Boolean = this == LOCAL_OPML
}