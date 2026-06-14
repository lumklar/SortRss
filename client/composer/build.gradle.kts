import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.compose.multiplatform)
}

//TODO 改为公共方法，传入key，value
val dataFlavor = (System.getenv("FLAVOR_DATA") ?: project.findProperty("flavor.data") as? String ?: "network")

kotlin {
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
                    "mock" -> implementation(project(":client:impl-data:mock"))
                    "network" -> implementation(project(":client:impl-data:network"))
                }
            }
        }
        val commonTest by getting {
            dependencies {

            }
        }
    }
}
