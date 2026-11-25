import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kmp.library)
    alias(libs.plugins.kotlin.kapt)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

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

    android {
        namespace = "io.github.lumklar.sortrss.common.api"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
        androidResources {
            enable = false
        }
        withHostTest {
            isIncludeAndroidResources = true
        }
    }

    // 配置源集（source sets）
    sourceSets {
        val commonTest = getByName("commonTest") {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }
}

dependencies {
    add("kapt", libs.therapi.scribe)
}

// scribe 只服务于 JVM 侧（server:adaptor 的 springdoc 读取其生成的 javadoc 元数据）。
// AGP 9 的 Android-KMP 插件与 kotlin-kapt 不兼容（kaptAndroidMain 的
// kotlinCompileDestinationDirectory provider 无值），故禁用 Android/HostTest 上的 kapt 任务，
// 仅保留 kaptKotlinJvm 生成 Swagger 所需元数据。
listOf(
    "kaptAndroidMain",
    "kaptGenerateStubsAndroidMain",
    "kaptAndroidHostTest",
    "kaptGenerateStubsAndroidHostTest"
).forEach { taskName ->
    tasks.matching { it.name == taskName }.configureEach { enabled = false }
}

// 生成源目录为所有 target 共享，显式声明顺序避免隐式依赖校验失败
tasks.matching { it.name == "compileAndroidMain" }.configureEach {
    mustRunAfter("kaptKotlinJvm")
}
