plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

kotlin {
    // 配置 Kotlin/JS target
    js(IR) {
        // 以浏览器为目标环境
        browser()
        // 明确告诉编译器，需要生成可执行的 .js 文件
        binaries.executable()
    }

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
        val commonMain by getting {
            dependencies {
                implementation(project(":client:ui"))
//                implementation(project(":client:network-impl"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }
}
