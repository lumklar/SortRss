import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
plugins {
    alias(libs.plugins.kotlin.multiplatform)
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

    // 配置源集（source sets）
    sourceSets {
        val commonMain = getByName("commonMain") {
            dependencies {
                implementation(project(":common:domain"))
//                implementation(libs.kotlin.logging)
            }
        }
        val commonTest = getByName("commonTest") {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }
}
