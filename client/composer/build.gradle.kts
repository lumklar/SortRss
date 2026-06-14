import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.compose.multiplatform)
}

interface StringEnum {
    val value: String
}

// 实现接口
enum class DataFlavor(override val value: String) : StringEnum {
    MOCK("mock"),
    NETWORK("network")
}

inline fun <reified T : Enum<T>> Project.getFlavorEnum(
    propertyKey: String,
    default: T,
    noinline valueMapper: (T) -> String = { (it as? StringEnum)?.value ?: it.name.lowercase() }
): T {
    val envKey = propertyKey.uppercase().replace(".", "_")
    val rawValue = (System.getenv(envKey) ?: findProperty(propertyKey) as? String)?.lowercase()

    if (rawValue == null) return default

    val matched = enumValues<T>().find { enumValue ->
        valueMapper(enumValue).equals(rawValue, ignoreCase = true)
    }
    return matched ?: default.also {
        logger.warn("未知的配置值 '$rawValue' (key=$propertyKey)，使用默认值 ${default.name}")
    }
}

kotlin {
    val dataFlavor = getFlavorEnum("flavor.data", DataFlavor.NETWORK)

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
                implementation(project(":client:ui"))
                implementation(libs.compose.runtime)
                implementation(libs.kotlin.insert)
                when (dataFlavor) {
                    DataFlavor.MOCK -> implementation(project(":client:impl-data:mock"))
                    DataFlavor.NETWORK -> implementation(project(":client:impl-data:network"))
                }
            }
        }
        val commonTest by getting {
            dependencies {

            }
        }
    }
}
