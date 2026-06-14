import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.compose.multiplatform)
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }

    jvm{

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
                implementation(libs.compose.runtime)
                implementation(libs.compose.foundation)
                implementation(libs.compose.material3)
                implementation(libs.compose.ui)
                implementation(libs.compose.components.resources)
//                implementation(libs.compose.uiToolingPreview)
                implementation(libs.androidx.lifecycle.viewmodelCompose)
                implementation(libs.androidx.lifecycle.runtimeCompose)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)

            }
        }
        val jsMain by getting {
            dependencies {
                implementation(libs.wrappers.browser)
            }
        }

        // ===================== WasmJs 浏览器平台：仅这里引入 DOM 依赖 =====================
        val wasmJsMain by getting {
            dependencies {
            }
        }
    }
}
