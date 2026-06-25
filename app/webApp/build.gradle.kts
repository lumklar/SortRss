import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.compose.multiplatform)
}

kotlin {
    // 配置 Kotlin/JS target
    js {
        // 以浏览器为目标环境
        browser()
        // 明确告诉编译器，需要生成可执行的 .js 文件
        binaries.executable()
    }

    @OptIn(ExperimentalWasmDsl::class)
    // 配置 Kotlin/Wasm target
    wasmJs {
        // 浏览器配置
        browser()
        // 明确告诉编译器，需要生成可执行文件
        binaries.executable()
    }

    // 配置源集（source sets）
    sourceSets {
        // 公共代码（所有平台共享）
        val commonMain = getByName("commonMain") {
            dependencies {
                implementation(project(":client:composer"))
                implementation(libs.compose.ui)
            }
        }
        val commonTest = getByName("commonTest") {
            dependencies {
            }
        }
    }
}
