//pluginManagement {
//    repositories {
//        google()
//        mavenCentral()
//        gradlePluginPortal()
//    }
//}

// 根项目名称
rootProject.name = "sortrss"

// 包含子模块
//include(":app")
include(":app:webApp")
//include(":client")
include(":client:contract")
include(":client:networkImpl")
include(":client:ui")
//include(":common")
include(":common:api")
include(":common:domain")
include(":common:infrastructure")
include(":common:shared")
include(":server")
