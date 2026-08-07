repositories {
    val isCI = providers.environmentVariable("CI").map { it.toBoolean() }.getOrElse(false)
    mavenLocal()
    if (isCI) {
        mavenCentral()
        gradlePluginPortal()
        google()
    } else {
        // 本地开发环境：使用阿里云镜像加速
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        maven { url = uri("https://maven.aliyun.com/repository/google") }
        // 备用官方仓库（防止阿里云缺包）
        mavenCentral()
        gradlePluginPortal()
        google()
    }
}

plugins {
    `kotlin-dsl`
    alias(libs.plugins.ben.manes.versions)
}

dependencies {
    implementation(gradleApi())
    implementation(libs.commons.compress)

    // 按需添加可选压缩库（只添加你实际会用到的）
    implementation(libs.xz)           // XZ / LZMA
    implementation(libs.zstd.jni)     // Zstandard
}

tasks.named<com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask>("dependencyUpdates") {
    revision = "release"          // 仅检查稳定版（正式发布版）
    outputFormatter = "html"      // 输出 HTML 格式报告（仅生成 HTML）
}
