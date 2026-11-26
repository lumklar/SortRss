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

// 配置 Spring Boot AOT 处理，提前声明所有 Profile
tasks.withType<org.springframework.boot.gradle.tasks.aot.ProcessAot> {
    // 将 dev, test, pro 全部声明，这样它们的 Bean 才会被包含在镜像中。
    // 注意：pro 配置会关闭 springdoc，而 @ConditionalOnProperty 在 AOT 期即固化，
    // 若不覆盖则生成的镜像里根本没有 OpenAPI / Swagger UI 的 Bean，dev 下也无法访问文档。
    args(
        "--spring.profiles.active=dev,test,pro",
        "--springdoc.api-docs.enabled=true",
        "--springdoc.swagger-ui.enabled=true"
    )
}

graalvmNative {
    binaries {
        named("main") {
            buildArgs.add("-Ob")
            // Spring Boot 4.2 起 spring-core 改用真实 commons-logging 1.4，
            // 默认 Log4jApiLogFactory 会经 log4j-to-slf4j 在启动极早期触碰
            // org.slf4j.MarkerFactory，native 下 Provider 尚未绑定导致
            // "Could not initialize class org.slf4j.MarkerFactory"。
            // 强制使用 commons-logging 自带的 SLF4J 直连工厂，绕开 log4j 桥接。
            buildArgs.add("-Dorg.apache.commons.logging.LogFactory=org.apache.commons.logging.impl.Slf4jLogFactory")
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
