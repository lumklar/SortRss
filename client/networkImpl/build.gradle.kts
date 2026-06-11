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
                implementation(project(":client:contract"))
                implementation(project(":common:api"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }
}
