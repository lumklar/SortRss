plugins {
    `kotlin-dsl`
}

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
