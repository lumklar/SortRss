plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.kapt)
//    id("com.google.devtools.ksp") version "2.3.9"
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(8)
    }
}

kotlin {
    // 仅配置 JVM 目标
    jvm {
        compilations.all {

        }
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
                compileOnly(libs.therapi.scribe)
            }
        }
    }
}
dependencies {
    add("kapt", libs.therapi.scribe)
}
