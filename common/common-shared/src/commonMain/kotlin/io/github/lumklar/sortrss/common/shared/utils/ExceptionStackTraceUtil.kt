package io.github.lumklar.sortrss.common.shared.utils

/**
 * 异常堆栈格式化工具（最终版：数组格式，无\r\n）
 */
object ExceptionStackTraceUtil {

    // 过滤框架/系统包，只保留业务代码
    private val EXCLUDE_PACKAGES = listOf<String>(
//        "java.", "javax.", "sun.", "jdk.",
//        "kotlin.", "kotlinx.",
//        "org.springframework.", "org.apache.", "org.hibernate.",
//        "com.sun.", "net.sf.", "io.netty.",
//        "java.lang.reflect.", "jdk.internal."
    )

    /**
     * 🔥 核心：返回 List<String> 数组，每行一个堆栈，无任何换行符
     */
    fun formatStackTrace(throwable: Throwable?, maxFrames: Int = 15): List<String> {
        val lines = mutableListOf<String>()
        if (throwable == null) {
            lines.add("无异常堆栈")
            return lines
        }

        // 1. 基础信息
        lines.add("【异常类型】: ${throwable.javaClass.simpleName}")
        lines.add("【异常信息】: ${throwable.message ?: "无消息"}")
        lines.add("【业务堆栈】: ")

        // 2. 过滤并添加业务栈帧
        val validFrames = throwable.stackTrace
            .filterNot { frame -> EXCLUDE_PACKAGES.any { frame.className.startsWith(it) } }
            .take(maxFrames)

        validFrames.forEachIndexed { index, frame ->
            lines.add(
                "  ${index + 1}. ${frame.className.substringAfterLast(".")}#${frame.methodName} (行号: ${frame.lineNumber})"
            )
        }

        // 3. 根因异常（Caused by）
        throwable.cause?.let { cause ->
            lines.add("【根因异常】: ${cause.javaClass.simpleName} - ${cause.message ?: "无消息"}")
            cause.stackTrace
                .filterNot { frame -> EXCLUDE_PACKAGES.any { frame.className.startsWith(it) } }
                .take(3)
                .forEachIndexed { index, frame ->
                    lines.add(
                        "  ${index + 1}. ${frame.className.substringAfterLast(".")}#${frame.methodName} (行号: ${frame.lineNumber})"
                    )
                }
        }

        return lines
    }
}