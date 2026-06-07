plugins {
    kotlin("multiplatform")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(8)
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
                api("io.github.oshai:kotlin-logging:8.0.02")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test")) // 公共测试框架
            }
        }

        // JVM 特定代码
        val jvmMain by getting {
            dependencies {
                runtimeOnly("io.github.oshai:kotlin-logging-jvm:8.0.02")
            }
        }
        val jvmTest by getting {
            dependencies {
                runtimeOnly("org.slf4j:slf4j-simple:2.0.18")
            }
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
