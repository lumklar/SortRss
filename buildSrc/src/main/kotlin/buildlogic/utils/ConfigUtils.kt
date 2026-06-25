package buildlogic.utils

import org.gradle.api.Project

/**
 * 获取配置值，严格按照以下优先级：
 * 1. 命令行 -P 参数（小写点分隔形式，如 `database.url`）
 * 2. 环境变量（大写，下划线分隔，如 `DATABASE_URL`）
 * 3. gradle.properties 文件中的属性（小写点分隔形式）
 *
 * 返回结果统一转换为小写；若所有来源均无值，则返回 null。
 */
fun Project.getConfigString(key: String): String? {
    // 1. 命令行项目属性（最高优先级） - 修复空字符串问题
    var propertyKey = EnvPropertyConverter.envToProperty(key)
    gradle.startParameter.projectProperties[propertyKey]
        ?.takeIf { it.isNotBlank() }
        ?.let { return it.lowercase() }

    // 2. 系统属性（新增来源，优先级介于环境变量和文件之间）
    System.getProperty(propertyKey)
        ?.takeIf { it.isNotBlank() }
        ?.let { return it.lowercase() }

    // 3. 环境变量（次高优先级） - 修复 NPE 风险
    EnvPropertyConverter.propertyToEnv(propertyKey).let { envKey ->
        System.getenv(envKey)
            ?.takeIf { it.isNotBlank() }
            ?.let { return it.lowercase() }
    }

    // 4. Gradle 属性文件（最低优先级） - 修复空字符串问题
    providers.gradleProperty(propertyKey).orNull
        ?.takeIf { it.isNotBlank() }
        ?.let { return it.lowercase() }

    return null
}

/**
 * 获取必需的配置值，若不存在则抛出错误
 * @param propertyKey 属性键
 * @return 配置值的小写形式
 * @throws GradleException 如果配置不存在
 */
fun Project.getRequiredConfigString(propertyKey: String): String {
    return getConfigString(propertyKey)
        ?: error("未找到必需配置项 '$propertyKey'，请设置命令行参数、环境变量或 gradle.properties。")
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

/**
 * 将字符串解析为布尔值，支持常见表示：
 * - true: "true", "1", "yes", "on"（不区分大小写）
 * - false: "false", "0", "no", "off"（不区分大小写）
 * 若无法解析则返回 null
 */
private fun String.toBooleanStrictOrNull(): Boolean? = when (lowercase()) {
    "true", "1", "yes", "on" -> true
    "false", "0", "no", "off" -> false
    else -> null
}

/**
 * 获取布尔配置值（优先级同 getConfigString），若未设置或无法解析则返回 null
 * @param propertyKey 属性键（如 "feature.enabled"）
 * @return 配置值的布尔表示，或 null
 */
fun Project.getConfigBoolean(propertyKey: String): Boolean? {
    return getConfigString(propertyKey)?.toBooleanStrictOrNull()
}

/**
 * 获取布尔配置值，若不存在或无法解析则返回指定的默认值
 * @param propertyKey 属性键
 * @param defaultValue 默认值
 * @return 配置值的布尔表示，或默认值
 */
fun Project.getConfigBoolean(propertyKey: String, defaultValue: Boolean): Boolean {
    return getConfigBoolean(propertyKey) ?: defaultValue
}
