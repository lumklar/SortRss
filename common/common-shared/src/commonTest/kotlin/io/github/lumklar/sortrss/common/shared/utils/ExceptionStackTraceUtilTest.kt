package io.github.lumklar.sortrss.common.shared.utils

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ExceptionStackTraceUtilTest {

    // 1. 测试 null 输入
    @Test
    fun `given null throwable, return default message`() {
        val result = ExceptionStackTraceUtil.formatStackTrace(null)
        assertEquals(listOf("无异常堆栈"), result)
    }

    // 2. 测试普通异常，能正确解析类型和消息
    @Test
    fun `given normal exception, extract type and message correctly`() {
        val testMsg = "业务参数错误"
        val exception = RuntimeException(testMsg)

        val result = ExceptionStackTraceUtil.formatStackTrace(exception)

        // 断言包含 异常类型、异常信息（不测完整文案）
        assertTrue(result.any { it.contains("RuntimeException") })
        assertTrue(result.any { it.contains(testMsg) })
        // 断言有堆栈输出
        assertTrue(result.size > 3)
    }

    // 3. 测试最大堆栈行数限制（核心逻辑）
    @Test
    fun `given max frames limit, truncate stack correctly`() {
        val exception = RuntimeException("测试截断")
        val maxFrames = 2

        val result = ExceptionStackTraceUtil.formatStackTrace(exception, maxFrames)
        // 统计堆栈行（以 数字+. 开头）
        val stackLineCount = result.count { it.trim().matches(Regex("""^\d+\..*""")) }

        // 断言不超过最大限制
        assertTrue(stackLineCount <= maxFrames)
    }

    // 4. 测试嵌套异常（Caused by 根因）
    @Test
    fun `given caused by exception, extract root cause correctly`() {
        val causeMsg = "数据库连接失败"
        val cause = IllegalArgumentException(causeMsg)
        val exception = RuntimeException("业务处理失败", cause)

        val result = ExceptionStackTraceUtil.formatStackTrace(exception)

        // 断言识别到根因
        assertTrue(result.any { it.contains("根因异常") })
        assertTrue(result.any { it.contains("IllegalArgumentException") })
        assertTrue(result.any { it.contains(causeMsg) })
    }

    // 5. 测试输出无换行符（格式要求）
    @Test
    fun `given any exception, result has no line breaks`() {
        val exception = RuntimeException("测试格式")
        val result = ExceptionStackTraceUtil.formatStackTrace(exception)

        // 断言所有行都没有 \r \n 换行符
        result.forEach {
            assertTrue(!it.contains("\n") && !it.contains("\r"))
        }
    }
}
