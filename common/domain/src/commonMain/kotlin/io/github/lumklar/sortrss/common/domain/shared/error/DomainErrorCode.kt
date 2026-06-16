package io.github.lumklar.sortrss.common.domain.shared.exception.code

/**
 * 领域层业务错误码（DDD 核心）
 * 编码规则：3 + 模块码(2位) + 序号(2位)
 * 仅内部使用，不对外暴露
 */
interface DomainErrorCode {
    val code: Int
    val msg: String
}