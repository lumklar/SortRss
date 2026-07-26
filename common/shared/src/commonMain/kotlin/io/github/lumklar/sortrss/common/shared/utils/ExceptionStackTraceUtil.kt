package io.github.lumklar.sortrss.common.shared.utils

/**
 * 异常堆栈格式化工具（纯 Kotlin Common，使用标准库 API）
 */
object ExceptionStackTraceUtil {

    /**
     * 返回 List<String>，包含异常类型、消息和堆栈跟踪行（最多 maxFrames 行）。
     * 使用 [Throwable.stackTraceToString] 获取原生堆栈字符串，再按行拆分。
     * 不进行包过滤，因为 Common 中无法解析堆栈元素。
     */
    fun formatStackTrace(throwable: Throwable?, maxFrames: Int = 15): List<String> {
        if (throwable == null) {
            return listOf("无异常堆栈")
        }

        val lines = mutableListOf<String>()
        lines.add("【异常类型】: ${throwable::class.simpleName}")
        lines.add("【异常信息】: ${throwable.message ?: "无消息"}")
        lines.add("【堆栈跟踪】:")

        // 获取完整堆栈字符串，按换行分割，过滤空行，取前 maxFrames 行
        val fullStack = throwable.stackTraceToString()
        val stackLines = fullStack
            .split("\n")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .take(maxFrames)

        lines.addAll(stackLines)

        // 注意：stackTraceToString 已经包含了 cause 信息，无需单独处理
        return lines
    }
}