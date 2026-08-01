plugins {
    // Kotlin 核心插件
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    // Spring Boot 生态
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    //其他插件
    alias(libs.plugins.dependency.check.jvm)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

dependencyCheck {
    // 在此处指定NVD API Key的值
    nvd {
        apiKey.set(
            (project.findProperty("nvdApiKey") as? String) ?: System.getenv("NVD_API_KEY")
        )
    }
}


dependencies {
    implementation(project(":common:domain"))
    implementation(project(":common:foundation"))

    // Kotlin
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.reflect)

    //日志
    implementation(libs.kotlin.logging.jvm)

    // spring boot
    implementation(libs.spring.boot.starter)

    //事务
    implementation(libs.spring.tx)

    // 缓存
    implementation(libs.spring.boot.starter.cache)
    runtimeOnly(libs.caffeine)

    // RSS
    implementation(libs.rome)
    implementation(libs.rome.modules)

    // 测试
    testImplementation(libs.spring.boot.starter.test)
}
