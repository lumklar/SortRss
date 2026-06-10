plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.compose)
}

kotlin {
    js(IR) {
        browser()
    }

    wasmJs {
        browser()
    }

    // 配置源集（source sets）
    sourceSets {
        // 公共代码（所有平台共享）
        val commonMain by getting {
            dependencies {
                implementation(project(":client:client-contract"))
                implementation(libs.compose.runtime)
                implementation(libs.compose.foundation)
                implementation(libs.compose.material3)
                implementation(libs.compose.ui)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)

            }
        }
        val jsMain by getting {
            dependencies {
                implementation(libs.compose.js)
            }
        }

        // ===================== WasmJs 浏览器平台：仅这里引入 DOM 依赖 =====================
        val wasmJsMain by getting {
            dependencies {
                implementation(libs.compose.wasm)
            }
        }
    }
}
