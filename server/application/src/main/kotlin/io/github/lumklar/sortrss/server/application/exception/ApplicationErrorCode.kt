package io.github.lumklar.sortrss.server.application.exception

/**
 * 应用层异常码。
 *
 * 码段约定（与领域码 `DomainErrorCode` 隔离，避免上层契约泄漏领域内部编号）：
 * - `1000` ~ `1999`：应用层异常码
 * - 领域码保持 `2xxx` ~ `6xxx`，仅作为 [ApplicationException.domainCode] 溯源信息保留
 *
 * 新增码时不要复用或改写已发布数值；[code] 即对外（`ApiExtra.errorCode`）暴露的值。
 */
enum class ApplicationErrorCode(val code: Int, val msg: String) {
    /** 领域规则未满足，由 [ApplicationException.from] 包装领域异常产生 */
    DOMAIN_REJECTED(1001, "业务规则校验未通过"),

    /** 应用层自身校验或流程失败 */
    APPLICATION_REJECTED(1002, "应用处理失败"),

    /** 基础设施（数据库、网络等）故障 */
    INFRASTRUCTURE_ERROR(1003, "基础设施故障"),

    /** 无法归类的异常 */
    UNKNOWN(1099, "未知应用错误")
}
