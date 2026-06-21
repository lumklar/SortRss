allprojects {
    repositories {
        mavenLocal()
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        maven { url = uri("https://maven.aliyun.com/repository/google") }
        mavenCentral()
//        google()
    }

    buildscript {
        repositories {
            mavenLocal()
            maven { url = uri("https://maven.aliyun.com/repository/public") }
            maven { url = uri("https://maven.aliyun.com/repository/google") }
            mavenCentral()
//            google()
        }
    }
}

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
extra["tomcat.version"] = "11.0.22"

// 全局项目信息
allprojects {
    group = "com.github.lumklar"
    version = "0.0.1-SNAPSHOT"
    description = "Sort-Rss multi-module project"
}

subprojects {

}

// 子项目配置
subprojects {
    // TODO 修复这个问题。给所有子项目添加缺失的任务（终极保险）
    tasks.register("prepareKotlinBuildScriptModel") {}
    tasks.register("prepareKotlinIdeaImport") {}
}
