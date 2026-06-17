package io.github.lumklar.sortrss.client.contract.data.repository

import io.github.lumklar.sortrss.client.contract.data.model.User
import kotlinx.coroutines.flow.StateFlow

interface UserRepository {
    /**
     * 当前用户信息的只读 StateFlow，UI 通过 collectAsState 自动订阅
     */
    val user: StateFlow<User?>

    /**
     * 从数据源（网络/本地）刷新用户信息
     */
    suspend fun refreshUser()

    /**
     * 模拟更新用户名（演示数据变更自动刷新 UI）
     */
    suspend fun updateUsername(newUsername: String)
}
