package io.github.lumklar.sortrss.common.api.dto.common

/**
 * 通用分页响应结果
 * @param T 业务数据泛型
 * @param records 当前页数据列表
 * @param total 总记录数
 * @param current 当前页码
 * @param size 每页条数
 */
data class PageResp<T>(
    /**
     *分页数据列表
     */
    val records: List<T> = emptyList(),
    /**
     *总条数
     */
    val total: Long = 0,
    /**
     *当前页
     */
    val current: Long = 1,
    /**
     *每页条数
     */
    val size: Long = 10
) {
    /**
     *自动计算总页数
     */
    val pages: Long
        get() = if (size == 0L) 0 else (total + size - 1) / size

    // 空数据快捷构造
    companion object {
        fun <T> empty(req: PageReq): PageResp<T> {
            return PageResp(
                current = req.pageNum,
                size = req.pageSize
            )
        }
    }
}

