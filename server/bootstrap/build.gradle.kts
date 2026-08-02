import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    // Kotlin 核心插件
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    // Spring Boot 生态
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    //其他插件
    alias(libs.plugins.graalvm.native)
    alias(libs.plugins.dependency.check.jvm)
}

tasks.processResources {
    // 声明一个输入属性，便于缓存自动重新构建
    val versionInput = project.version.toString()
    inputs.property("appVersion", versionInput)

    filesMatching("application*.yml") {
        filter<ReplaceTokens>(
            "tokens" to mapOf(
                "app.version" to versionInput
            )
        )
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.bootJar {
}

tasks.withType<Test> {
    useJUnitPlatform()
}

graalvmNative {
    binaries {
        named("main") {
            buildArgs.add("-Ob")
        }
    }
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
    implementation(project(":server:adaptor"))
    implementation(project(":server:application"))
    implementation(project(":server:infrastructure"))

    // Kotlin
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.reflect)

    //日志
    implementation(libs.kotlin.logging.jvm)

    // spring boot
    implementation(libs.spring.boot.starter)

    // 测试
    testImplementation(libs.spring.boot.starter.test)
}
