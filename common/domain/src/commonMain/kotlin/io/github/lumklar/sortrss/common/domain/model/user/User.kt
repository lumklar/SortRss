package io.github.lumklar.sortrss.common.domain.model.user

import io.github.lumklar.sortrss.common.domain.shared.ability.PasswordEncoder
import kotlin.uuid.Uuid

class User private constructor(
    val id: UserId,
    username: Username,
    private var password: Password?,
    registrationSource: RegistrationSource
) {
    // 公开只读属性（外部可访问，但不能直接赋值）
    var username: Username = username
        private set

    var registrationSource: RegistrationSource = registrationSource
        private set

    companion object {
        /**
         * 本地注册（用户名 + 密码）
         */
        fun register(
            id: UserId,
            rawUsername: String,
            plainPassword: String,
            encoder: PasswordEncoder,
            policy: PasswordPolicy,
        ): User {
            val username = Username.fromBusinessString(rawUsername)
            val password = Password.encode(plainPassword, encoder, policy)
            return User(id, username, password, RegistrationSource.LOCAL)
        }

        /**
         * 第三方注册（无密码，用户名使用用户id）
         */
        internal fun registerExternal(
            id: UserId
        ): User {
            val username = Username.fromUuid(id.value)
            return User(id, username, null, RegistrationSource.EXTERNAL)
        }

        /**
         * 匿名注册（无密码）
         */
        internal fun registerAnonymous(
            id: UserId
        ): User {
            val username = Username.fromUuid(id.value)
            return User(id, username, null, RegistrationSource.ANONYMOUS)
        }

        /**
         * 从持久化重建
         */
        fun reconstruct(
            id: Uuid,
            username: String,
            passwordHash: String?,
            registrationSource: RegistrationSource
        ): User {
            val userId = UserId(id)
            val uname = Username.fromAny(username)
            val pwd = passwordHash?.let { Password.fromHash(it) }
            return User(userId, uname, pwd, registrationSource)
        }
    }

    fun hasPassword(): Boolean = password != null

    fun verifyPassword(raw: String, encoder: PasswordEncoder): Boolean =
        password?.matches(raw, encoder) ?: false

    /**
     * 设置初始密码（仅当用户尚无密码时使用）
     */
    fun setPassword(raw: String, encoder: PasswordEncoder, policy: PasswordPolicy) {
        require(password == null) { "用户已有密码，请使用修改密码功能" }
        password = Password.encode(raw, encoder, policy)
    }

    /**
     * 修改密码（需提供旧密码）
     */
    fun changePassword(
        oldRaw: String,
        newRaw: String,
        encoder: PasswordEncoder,
        policy: PasswordPolicy
    ) {
        if (password == null) {
            throw UserHasNoPasswordException("用户未设置密码，请使用 setPassword")
        }
        if (!verifyPassword(oldRaw, encoder)) {
            throw OldPasswordMismatchException()
        }
        password = Password.encode(newRaw, encoder, policy)
    }

    fun getPasswordHash(): String? = password?.asHash()

    // 修改用户名（唯一入口，执行严格验证）
    fun changeUsername(newUsername: String) {
        this.username = Username.fromBusinessString(newUsername)
    }
    /**
     * 将匿名用户升级为外部来源（不涉及密码）。
     * 用于第三方绑定场景。
     */
    internal fun upgradeFromAnonymousToExternal() {
        ensureAnonymous()
        this.registrationSource = RegistrationSource.EXTERNAL
    }

    /**
     * 匿名用户通过设置密码升级为本地注册用户。
     * 用于本地密码设置场景。
     */
    fun upgradeFromAnonymousWithPassword(
        rawPassword: String,
        encoder: PasswordEncoder,
        policy: PasswordPolicy
    ) {
        ensureAnonymous()
        // 先设置密码（如果失败会抛出异常，来源不会改变）
        setPassword(rawPassword, encoder, policy)
        this.registrationSource = RegistrationSource.LOCAL
    }

    /**
     * 私有辅助方法：检查当前用户是否为匿名，否则抛出异常。
     */
    private fun ensureAnonymous() {
        require(this.registrationSource == RegistrationSource.ANONYMOUS) {
            "只有匿名用户可以执行此操作"
        }
    }
}