pluginManagement {
    repositories {
        val isCI = providers.environmentVariable("CI").map { it.toBoolean() }.getOrElse(false)
        mavenLocal()
        if (isCI) {
            mavenCentral()
            gradlePluginPortal()
            google()
        } else {
            // 本地开发环境：使用阿里云镜像加速
            maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") }
            maven { url = uri("https://maven.aliyun.com/repository/google") }
            // 备用官方仓库（防止阿里云缺包）
            mavenCentral()
            gradlePluginPortal()
        }
    }
}

//dependencyResolutionManagement {
//    val isCI = providers.environmentVariable("CI").map { it.toBoolean() }.getOrElse(false)
//    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
//    repositories {
//        mavenLocal()
//        if (isCI) {
//            mavenCentral()
//            google()
//            maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
//        } else {
//            maven { url = uri("https://maven.aliyun.com/repository/public") }
//            maven { url = uri("https://maven.aliyun.com/repository/google") }
//            mavenCentral()
//            google()
//            // 本地保留 Compose 依赖仓库（Skiko 只能在这下载）
//            maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
//        }
//    }
//}

// 根项目名称
rootProject.name = "sortrss"

// 包含子模块
//include(":app")
include(":app:webApp")
include(":app:desktopApp")
//include(":client")
include(":client:composer")
//include(":client:contract")
include(":client:contract:all")
include(":client:contract:data")
include(":client:impl-data:network")
include(":client:impl-data:mock")
include(":client:impl-data:local")
//include(":client:ui")
include(":client:ui:app")
include(":client:ui:components")
include(":client:ui:navigation")
include(":client:ui:feature:feed")
include(":client:ui:theme")
//include(":common")
include(":common:api")
include(":common:domain")
include(":common:infrastructure")
include(":common:shared")
include(":server")
