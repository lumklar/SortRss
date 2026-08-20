package io.github.lumklar.sortrss.common.domain.model.user

enum class RegistrationSource {
    /**
     * 本地注册
     */
    LOCAL,

    /**
     * 匿名用户
     */
    ANONYMOUS,

    /**
     * 外部账户
     */
    EXTERNAL
}
