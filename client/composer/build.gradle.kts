import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import getRequiredFlavorEnum   // 导入扩展函数
import DataFlavor                    // 导入枚举

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.compose.multiplatform)
}

kotlin {
    val dataFlavor = getRequiredFlavorEnum<DataFlavor>("flavor.data")

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
