package buildlogic.flavors

import buildlogic.utils.getConfigString
import buildlogic.utils.getRequiredConfigString
import org.gradle.api.Project

/**
 * 将原始字符串匹配到枚举常量
 */
fun <T> matchEnum(rawValue: String, enumConstants: Array<T>): T?
        where T : Enum<T>, T : StringEnum {
    return enumConstants.find { it.value.equals(rawValue, ignoreCase = true) }
}

/**
 * 从配置中获取枚举（带默认值）
 * @param propertyKey 配置键
 * @param default 默认枚举值（若配置缺失或无效则返回此值）
 */
inline fun <reified T> Project.getFlavorEnum(
    propertyKey: String,
    default: T
): T where T : Enum<T>, T : StringEnum {
    val rawValue = getConfigString(propertyKey)
    if (rawValue == null) return default

    val matched = matchEnum(rawValue, enumValues<T>())
    return matched ?: default.also {
        logger.warn("未知的配置值 '$rawValue'，使用默认值 ${default.name}")
    }
}

/**
 * 从配置中获取枚举（必须配置，否则报错）
 */
inline fun <reified T> Project.getRequiredFlavorEnum(propertyKey: String): T
        where T : Enum<T>, T : StringEnum {
    val rawValue = getRequiredConfigString(propertyKey)
    val matched = matchEnum(rawValue, enumValues<T>())
    return matched
        ?: error("未知的配置值 '$rawValue'，可接受的值：${enumValues<T>().joinToString { it.name.lowercase() }}")
}