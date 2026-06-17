package io.github.lumklar.sortrss.client.contract.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 所有 Repository 的基类，封装 StateFlow 状态管理，避免子类重复代码。
 * @param T 数据类型
 * @param initialValue 初始值（如 null、空列表等）
 */
abstract class BaseRepository<T>(
    initialValue: T
) {
    private val _state = MutableStateFlow(initialValue)
    val state: StateFlow<T> = _state.asStateFlow()

    /**
     * 子类实现：从数据源（网络/本地/模拟）获取最新数据
     */
    protected abstract suspend fun fetchFromSource(): T

    /**
     * 刷新数据：调用 fetchFromSource，并更新 _state
     */
    suspend fun refresh(): T {
        val newData = fetchFromSource()
        _state.value = newData
        return newData
    }

    /**
     * 直接更新状态（用于本地修改后同步）
     */
    protected fun updateState(newValue: T) {
        _state.value = newValue
    }
}
