plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.jpa)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.graalvm.native)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

tasks.bootJar {
}

graalvmNative {
    binaries {
        named("main") {
            buildArgs.add("-Ob")
        }
    }
}

dependencies {
    // 多模块依赖
    implementation(project(":common:common-domain"))
    implementation(project(":common:common-api"))
    implementation(project(":common:common-infrastructure"))
    implementation(project(":common:common-shared"))

    // Kotlin
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.reflect)

    //日志
    implementation(libs.kotlin.logging.jvm)

    // Web
    implementation(libs.spring.boot.starter.web)
    implementation(libs.springdoc.openapi.webmvc)

    // 缓存
    implementation(libs.spring.boot.starter.cache)
    runtimeOnly(libs.caffeine)

    // 数据持久化
    implementation(libs.spring.boot.starter.data.jpa)

    // 数据库驱动
    runtimeOnly(libs.sqlite.jdbc)
    runtimeOnly(libs.postgresql)
    runtimeOnly(libs.mysql.connector.j)

    // Lombok
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    // 加密
    implementation(libs.jbcrypt)

    // RSS
    implementation(libs.rome)
    implementation(libs.rome.modules)

    // 测试
    testImplementation(libs.spring.boot.starter.test)
}

tasks.withType<Test> {
    useJUnitPlatform()
}
