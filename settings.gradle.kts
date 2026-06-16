pluginManagement {
    repositories {
        mavenLocal()
        maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") }
        maven { url = uri("https://maven.aliyun.com/repository/google") }
        mavenCentral()
        gradlePluginPortal()
        google()
    }
}

//dependencyResolutionManagement {
//    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
//    repositories {
//        mavenLocal()
//        maven { url = uri("https://maven.aliyun.com/repository/public") }
//        maven { url = uri("https://maven.aliyun.com/repository/google") }
//        mavenCentral()
//        google()
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
