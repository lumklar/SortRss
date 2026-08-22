import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.serialization)
}


java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

kotlin {
    jvmToolchain(21)
    jvm {

    }

    js {
        browser()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }
//    iosX64()
//    iosArm64()
//    iosSimulatorArm64()
//    androidTarget()

    // 配置源集（source sets）
    sourceSets {
        val commonMain = getByName("commonMain") {
            dependencies {
                implementation(project(":common:domain"))
                implementation(project(":common:shared"))
            }
        }
        val commonTest = getByName("commonTest") {
            dependencies {
//                implementation(libs.kotlin.test)
//                implementation(libs.kotlin.test.common)
//                implementation(libs.kotlinx.coroutines.core)
//                implementation(libs.ktor.client.core)
            }
        }
    }
}

tasks.named("jvmTest", Test::class) {
    useJUnitPlatform()
}
