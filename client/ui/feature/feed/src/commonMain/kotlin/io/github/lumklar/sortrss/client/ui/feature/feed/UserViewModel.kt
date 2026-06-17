package io.github.lumklar.sortrss.client.ui.feature.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.lumklar.sortrss.client.contract.data.model.User
import io.github.lumklar.sortrss.client.contract.data.repository.UserRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    // 直接暴露 Repository 的 StateFlow，UI 订阅即可
    val user: StateFlow<User?> = userRepository.user

    /**
     * 手动刷新（例如下拉刷新时调用）
     */
    fun refresh() {
        viewModelScope.launch {
            userRepository.refreshUser()
        }
    }

    /**
     * 修改用户名（演示自动刷新）
     */
    fun updateUsername(newName: String) {
        viewModelScope.launch {
            userRepository.updateUsername(newName)
        }
    }
}
