package io.github.lumklar.sortrss.client.impl.data.mock

import io.github.lumklar.sortrss.client.contract.data.model.User
import io.github.lumklar.sortrss.client.contract.data.repository.BaseRepository
import io.github.lumklar.sortrss.client.contract.data.repository.UserRepository
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

class MockUserRepository : BaseRepository<User?>(null), UserRepository {

    override val user = state // 直接暴露基类的 state

    /**
     * 模拟从网络/数据库获取用户（这里固定返回一个初始用户）
     */
    override suspend fun fetchFromSource(): User? {
        // 模拟网络延迟
        delay(500.milliseconds)
        // 使用领域工厂方法创建用户（需提供 PasswordEncoder，这里使用假实现）
        return User.fromPersistence(
            id = 1L,
            username = "测试用户",
            passwordHash = "fakeHash"
        )
    }

    override suspend fun refreshUser() {
        refresh() // 调用基类方法，自动更新 state
    }

    override suspend fun updateUsername(newUsername: String) {
        // 模拟修改用户名：从当前用户对象生成新用户
        val current = state.value
        val updatedUser = if (current != null) {
            // 因为 User 不可变，用 fromPersistence 重建
            User.fromPersistence(
                id = current.id,
                username = newUsername,
                passwordHash = current.passwordHash
            )
        } else {
            // 若当前为 null，直接创建新用户
            User.fromPersistence(
                id = 1L,
                username = newUsername,
                passwordHash = "defaultHash"
            )
        }
        // 更新状态（UI 自动刷新）
        updateState(updatedUser)
    }
}
