package io.github.lumklar.sortrss.common.domain.model.user

interface PasswordPolicy {
    fun validate(plain: String) // 校验不通过抛异常
}
