plugins {
    // Kotlin 核心插件
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.jpa)
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
//    implementation(libs.spring.boot.starter.json)

    // 缓存
    implementation(libs.spring.boot.starter.cache)
    runtimeOnly(libs.caffeine)

    // 数据持久化
    implementation(libs.spring.boot.starter.data.jpa)

    // 数据库驱动
    runtimeOnly(libs.sqlite.jpa)
    runtimeOnly(libs.sqlite.jdbc)
    runtimeOnly(libs.postgresql)
    runtimeOnly(libs.mysql.connector.j)

    // Lombok
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    // 加密
    implementation(libs.jbcrypt)
    implementation(libs.fastjson2)

    // 测试
    testImplementation(libs.spring.boot.starter.test)
}