package buildlogic.utils

/**
 * 环境变量与 Gradle 属性键名转换工具
 *
 * 环境变量格式：全大写，单词间用下划线分隔，例如 `DATABASE_URL`
 * Gradle 属性格式：全小写，单词间用点分隔，例如 `database.url`
 */
object EnvPropertyConverter {

    /**
     * 将环境变量风格的键转换为 Gradle 属性风格的键
     * @param envKey 环境变量键，例如 "DATABASE_URL"
     * @return Gradle 属性键，例如 "database.url"
     */
    fun envToProperty(envKey: String): String {
        require(envKey.isNotBlank()) { "环境变量键不能为空" }
        return envKey.lowercase().replace('_', '.')
    }

    /**
     * 将 Gradle 属性风格的键转换为环境变量风格的键
     * @param propertyKey Gradle 属性键，例如 "database.url"
     * @return 环境变量键，例如 "DATABASE_URL"
     */
    fun propertyToEnv(propertyKey: String): String {
        require(propertyKey.isNotBlank()) { "Gradle 属性键不能为空" }
        return propertyKey.uppercase().replace('.', '_')
    }

    /**
     * 批量转换：环境变量 Map → Gradle 属性 Map
     * @param envMap 键为环境变量风格的 Map
     * @return 键为 Gradle 属性风格的 Map
     */
    fun convertEnvMapToPropertyMap(envMap: Map<String, String>): Map<String, String> {
        return envMap.mapKeys { (key, _) -> envToProperty(key) }
    }

    /**
     * 批量转换：Gradle 属性 Map → 环境变量 Map
     * @param propertyMap 键为 Gradle 属性风格的 Map
     * @return 键为环境变量风格的 Map
     */
    fun convertPropertyMapToEnvMap(propertyMap: Map<String, String>): Map<String, String> {
        return propertyMap.mapKeys { (key, _) -> propertyToEnv(key) }
    }
}
