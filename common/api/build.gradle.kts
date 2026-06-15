import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.kapt)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(8)
    }
}

kotlin {
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

            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
        val jvmMain by getting {
            dependencies {

            }
        }
    }
}

dependencies {
    add("kapt", libs.therapi.scribe)
}
