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
include(":app")
include(":client")
include(":client:client-contract")
include(":client:client-network-impl")
include(":client:client-ui")
include(":common")
include(":common:common-api")
include(":common:common-domain")
include(":common:common-infrastructure")
include(":common:common-shared")
include(":server")
