import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

// 定义生成目录
val generatedKotlinDir = layout.buildDirectory.dir("generated/kotlin/commonMain").get().asFile

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
        val commonMain = getByName("commonMain") {
            // 添加生成的源码目录
            kotlin.srcDir(generatedKotlinDir)
            dependencies {

            }
        }
        val commonTest = getByName("commonTest") {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }
}

tasks.register("generateVersionFile") {
    val version = project.version.toString()
    val outputDir = generatedKotlinDir.resolve("io/github/lumklar/sortrss/common/constants")
    val outputFile = outputDir.resolve("Version.kt")

    inputs.property("version", version)
    outputs.file(outputFile)

    doLast {
        // 删除整个包目录，避免旧文件残留
        if (outputDir.exists()) {
            outputDir.deleteRecursively()
        }
        outputDir.mkdirs()
        outputFile.writeText(
            """
            package io.github.lumklar.sortrss.common.shared.constants

            val APP_VERSION = "$version"
            """.trimIndent()
        )
    }
}

// 让所有 Kotlin 编译任务依赖生成任务，确保生成代码在编译前完成
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
    dependsOn("generateVersionFile")
}
