import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.compose.multiplatform)
}

val dataFlavor: String = project.properties["flavor.data"] as? String ?: "mock"

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
                implementation(project(":client:impl-data:network"))
//                implementation(project(":client:impl-data:mock"))
                implementation(project(":client:ui"))
                implementation(libs.compose.runtime)
            }
        }
        val commonTest by getting {
            dependencies {

            }
        }
    }
}
