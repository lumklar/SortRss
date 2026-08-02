import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

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

// ---- 动态生成构建信息 ----
tasks.register("generateBuildInfo") {
    val buildInfoMap = mapOf(
        "APP_VERSION" to project.version.toString(),
        "BUILD_TIME" to System.currentTimeMillis().toString(),
    )

    val outputDir = generatedKotlinDir.resolve("io/github/lumklar/sortrss/common/constants")
    val outputFile = outputDir.resolve("BuildInfo.kt")

    inputs.properties(buildInfoMap)
    outputs.file(outputFile)

    doLast {
        // 删除整个包目录，避免旧文件残留
        if (outputDir.exists()) {
            outputDir.deleteRecursively()
        }
        outputDir.mkdirs()

        // 使用 buildString 精确控制缩进，每行统一缩进 4 个空格
        val constantsCode = buildString {
//            appendLine("object BuildInfo {")
            buildInfoMap.entries.forEach { (key, value) ->
                appendLine("    const val $key = \"$value\"")  // 4 个空格缩进
            }
//            append("}")
        }

        outputFile.writeText(
            """
            package io.github.lumklar.sortrss.common.shared.constants
            
            object BuildInfo {
            // 自动生成的构建常量，请勿手动修改
            $constantsCode
            }
            """.trimIndent()
        )
    }
}

// 让所有 Kotlin 编译任务依赖生成任务
tasks.withType<KotlinCompile>().all {
    dependsOn("generateBuildInfo")
}
