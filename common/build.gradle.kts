plugins {
    kotlin("multiplatform") version "2.3.21"
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

kotlin {
    // 仅配置 JVM 目标
    jvm {}

    // 配置源集（source sets）
    sourceSets {
        // 公共代码（所有平台共享）
        val commonMain by getting {
            dependencies {
                // 这里放所有平台共享的依赖
                // 例如：implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test")) // 公共测试框架
            }
        }

        // JVM 特定代码
//        val jvmMain by getting {
//            dependencies {
                // 仅 JVM 需要的依赖
//            }
//        }
//        val jvmTest by getting {
//            dependencies {
                // 仅 JVM 测试需要的依赖
//            }
//        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}