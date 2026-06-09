plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.kapt) apply false
    alias(libs.plugins.kotlin.spring) apply false
    alias(libs.plugins.kotlin.jpa) apply false
    alias(libs.plugins.spring.boot) apply false
    alias(libs.plugins.spring.dependency.management) apply false
    alias(libs.plugins.graalvm.native) apply false
}

// 全局项目信息
allprojects {
    group = "com.github.lumklar"
    version = "0.0.1-SNAPSHOT"
    description = "Sort-Rss multi-module project"

    // 所有模块共用仓库
    repositories {
        mavenCentral()
        google()
    }
}

// 子项目配置
subprojects {
    // TODO 修复这个问题。给所有子项目添加缺失的任务（终极保险）
    tasks.register("prepareKotlinBuildScriptModel") {}
    tasks.register("prepareKotlinIdeaImport") {}
}
