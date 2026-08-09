import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.serialization)
}

base {
    archivesName = "common-foundation"
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
//                implementation(libs.kotlin.logging)
                implementation(libs.kotlin.serialization.core)
                implementation(libs.kotlin.serialization)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.json)
                implementation(libs.md)
                implementation(libs.kotlinx.coroutines.core)
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
        val jvmMain = getByName("jvmMain") {
            dependencies {
                implementation(libs.ktor.client.okhttp)
            }
        }
//        val androidMain = getByName("androidMain") {
//            dependencies {
//                implementation(libs.ktor.client.android)
//            }
//        }
//        val iosMain = getByName("iosMain") {
//            dependencies {
//                implementation(libs.ktor.client.darwin)
//            }
//        }
        val jvmTest = getByName("jvmTest"){
            dependencies {
                implementation(libs.wiremock)
                implementation(libs.kotlin.test.junit5)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.ktor.client.okhttp)
            }
        }
    }
}

tasks.named("jvmTest", Test::class) {
    useJUnitPlatform()
}
