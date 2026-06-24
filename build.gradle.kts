//allprojects {
//    repositories {
//        mavenLocal()
//        maven { url = uri("https://maven.aliyun.com/repository/public") }
//        maven { url = uri("https://maven.aliyun.com/repository/google") }
//        mavenCentral()
////        google()
//    }
//
//    buildscript {
//        repositories {
//            mavenLocal()
//            maven { url = uri("https://maven.aliyun.com/repository/public") }
//            maven { url = uri("https://maven.aliyun.com/repository/google") }
//            mavenCentral()
////            google()
//        }
//    }
//}

plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.kapt) apply false
    alias(libs.plugins.kotlin.spring) apply false
    alias(libs.plugins.kotlin.jpa) apply false
    alias(libs.plugins.spring.boot) apply false
    alias(libs.plugins.spring.dependency.management) apply false
    alias(libs.plugins.graalvm.native) apply false
    alias(libs.plugins.dependency.check.jvm) apply false
}

//TODO 写一个脚本+配置统一管理，防止泄露删除
//漏洞修复
//extra["tomcat.version"] = "11.0.22"

apply(from = "gradle/common.gradle.kts")
apply(from = "gradle/docker.gradle.kts")
