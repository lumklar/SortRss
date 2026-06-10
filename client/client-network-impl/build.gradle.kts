plugins {
    alias(libs.plugins.kotlin.multiplatform)
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
                implementation(project(":common:common-api"))

                implementation("io.ktor:ktor-client-core:2.3.11")
                implementation("io.ktor:ktor-client-js:2.3.11")
                implementation("io.ktor:ktor-client-content-negotiation:2.3.11")
                implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.11")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }
}
