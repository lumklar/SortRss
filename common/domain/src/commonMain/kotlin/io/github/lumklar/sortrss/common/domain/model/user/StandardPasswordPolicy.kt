package io.github.lumklar.sortrss.common.domain.model.user

/**
 * 标准密码策略实现
 * 规则（均可通过构造参数配置）：
 * - 最小长度（默认 8）
 * - 必须包含数字（默认开启）
 * - 必须包含小写字母（默认开启）
 * - 必须包含大写字母（默认开启）
 * - 必须包含特殊字符（默认开启）
 */
class StandardPasswordPolicy(
    private val minLength: Int = 8,
    private val requireDigit: Boolean = true,
    private val requireLowercase: Boolean = true,
    private val requireUppercase: Boolean = true,
    private val requireSpecial: Boolean = true
) : PasswordPolicy {

    override fun validate(plain: String) {
        if (plain.length < minLength) {
            throw PasswordPolicyViolationException("密码长度至少为 $minLength 位")
        }
        if (requireDigit && !plain.any { it.isDigit() }) {
            throw PasswordPolicyViolationException("密码必须包含数字")
        }
        if (requireLowercase && !plain.any { it.isLowerCase() }) {
            throw PasswordPolicyViolationException("密码必须包含小写字母")
        }
        if (requireUppercase && !plain.any { it.isUpperCase() }) {
            throw PasswordPolicyViolationException("密码必须包含大写字母")
        }
        if (requireSpecial && !plain.any { !it.isLetterOrDigit() }) {
            throw PasswordPolicyViolationException("密码必须包含特殊字符")
        }
    }
}