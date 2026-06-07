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

//TODO 版本都在这里统一管理？
plugins {
    kotlin("jvm") version "2.3.21" apply false
    kotlin("multiplatform") version "2.3.21" apply false
    kotlin("kapt") version "2.3.21" apply false
    kotlin("plugin.jpa") version "2.3.21" apply false
    kotlin("plugin.spring") version "2.3.21" apply false
}

// 子项目配置
subprojects {
    // TODO 修复这个问题。给所有子项目添加缺失的任务（终极保险）
    tasks.register("prepareKotlinBuildScriptModel") {}
    tasks.register("prepareKotlinIdeaImport") {}
}
