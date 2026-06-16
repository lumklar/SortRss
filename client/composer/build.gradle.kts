import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.compose.multiplatform)
}

/**
 * 风味枚举实现接口
 */
interface StringEnum {
    val value: String
}

fun Project.getRawFlavorValue(propertyKey: String): String? {
    val envKey = propertyKey.uppercase().replace(".", "_")
    return (System.getenv(envKey) ?: findProperty(propertyKey) as? String)?.lowercase()
}

fun <T> matchFlavorEnum(
    rawValue: String,
    enumConstants: Array<T>,
): T? where T : Enum<T>, T : StringEnum {
    return enumConstants.find { it.value.equals(rawValue, ignoreCase = true) }
}

inline fun <reified T> getFlavorEnum(
    propertyKey: String,
    default: T
): T where T : Enum<T>, T : StringEnum {
    val rawValue = getRawFlavorValue(propertyKey)
    if (rawValue == null) return default

    val matched = matchFlavorEnum(rawValue, enumValues<T>())
    return matched ?: default.also {
        logger.warn("未知的配置值 '$rawValue'，使用默认值 ${default.name}")
    }
}

fun <T> getFlavorEnum(
    propertyKey: String,
    enumClass: Class<T>
): T where T : Enum<T>, T : StringEnum {
    val rawValue = getRawFlavorValue(propertyKey)
        ?: error("未找到必需配置项 '$propertyKey'，请设置环境变量或项目属性。")
    val matched = matchFlavorEnum(rawValue, enumClass.enumConstants)
        ?: error("未知的配置值 '$rawValue'，可接受的值：${enumClass.enumConstants.joinToString { it.name.lowercase() }}")
    return matched
}

inline fun <reified T> getFlavorEnum(propertyKey: String): T
        where T : Enum<T>, T : StringEnum =
    getFlavorEnum(propertyKey, T::class.java)

/**
 * 数据风味
 */
enum class DataFlavor(override val value: String) : StringEnum {
    /**
     * mock数据
     */
    MOCK("mock"),

    /**
     * 网络远端实现
     */
    NETWORK("network"),

    /**
     * 本地实现
     */
    LOCAL("local")
}

kotlin {
    val dataFlavor = getFlavorEnum<DataFlavor>("flavor.data")

    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }

    jvm {
    }

    js {
        browser()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }

    // 配置源集（source sets）
    sourceSets {
        // 公共代码（所有平台共享）
        val commonMain by getting {
            dependencies {
                implementation(project(":client:contract:all"))
                implementation(project(":client:ui:app"))
                implementation(libs.compose.runtime)
                implementation(libs.kotlin.insert)
                when (dataFlavor) {
                    DataFlavor.MOCK -> implementation(project(":client:impl-data:mock"))
                    DataFlavor.NETWORK -> implementation(project(":client:impl-data:network"))
                    DataFlavor.LOCAL -> implementation(project(":client:impl-data:local"))
                }
            }
        }
        val commonTest by getting {
            dependencies {

            }
        }
    }
}
