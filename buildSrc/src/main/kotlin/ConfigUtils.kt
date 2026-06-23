import org.gradle.api.Project

/**
 * 获取配置值（优先环境变量，其次项目属性），返回小写字符串
 * @param propertyKey 属性键（如 "flavor.data"）
 * @return 配置值的小写形式，若不存在则返回 null
 */
fun Project.getConfigString(propertyKey: String): String? {
    val envKey = propertyKey.uppercase().replace(".", "_")
    return (System.getenv(envKey) ?: findProperty(propertyKey) as? String)?.lowercase()
}

/**
 * 获取必需的配置值，若不存在则抛出错误
 * @param propertyKey 属性键
 * @return 配置值的小写形式
 * @throws GradleException 如果配置不存在
 */
fun Project.getRequiredConfigString(propertyKey: String): String {
    return getConfigString(propertyKey)
        ?: error("未找到必需配置项 '$propertyKey'，请设置环境变量或项目属性。")
}

/**
 * 获取配置值，若不存在则返回指定的默认值
 * @param propertyKey 属性键
 * @param defaultValue 默认值（配置缺失时返回此值）
 * @return 配置值的小写形式，或默认值
 */
fun Project.getConfigString(propertyKey: String, defaultValue: String): String {
    return getConfigString(propertyKey) ?: defaultValue
}