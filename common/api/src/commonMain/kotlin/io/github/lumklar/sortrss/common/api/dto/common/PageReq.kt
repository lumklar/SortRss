package io.github.lumklar.sortrss.common.api.dto.common

/**
 * 通用分页请求参数
 */
open class PageReq {
    /**
     * 页码（默认第1页）
     */
    var pageNum: Long = 1;

    /**
     * 每页条数（默认10条）
     */
    var pageSize: Long = 10;

    /**
     * 数据库分页偏移量
     */
    val offset: Long
        get() = (pageNum - 1) * pageSize
}
