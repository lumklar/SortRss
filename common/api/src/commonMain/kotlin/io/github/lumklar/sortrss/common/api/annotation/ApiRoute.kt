package io.github.lumklar.sortrss.common.api.annotation

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ApiRoute(
    val value: String, // 路由路径
    val method: HttpMethod, // HTTP方法
    val authRequired: Boolean = true // 是否需要认证
)

// 配套枚举（common层）
enum class HttpMethod { GET, POST, PUT, DELETE, PATCH, HEAD, OPTIONS, TRACE }

