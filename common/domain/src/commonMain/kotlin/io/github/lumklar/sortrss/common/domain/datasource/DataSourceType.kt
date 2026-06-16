package io.github.lumklar.sortrss.common.domain.model.entity.enums

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

    fun isRemote(): Boolean = this != LOCAL_OPML

    fun supportLocalRefresh(): Boolean = this == LOCAL_OPML
}