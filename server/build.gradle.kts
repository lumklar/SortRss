plugins {
    kotlin("jvm")
//    kotlin("kapt")
    kotlin("plugin.jpa")
    kotlin("plugin.spring")
    id("org.springframework.boot") version "4.1.0-RC1"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.graalvm.buildtools.native") version "0.11.5"
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
    implementation(project(":common"))

    //kotlin反射依赖
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    //web
    implementation("org.springframework.boot:spring-boot-starter-web")
    //缓存
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("com.github.ben-manes.caffeine:caffeine")
    //持久层框架
    implementation("jakarta.persistence:jakarta.persistence-api")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    //数据库驱动
    runtimeOnly("org.xerial:sqlite-jdbc:3.53.1.0")
    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("com.mysql:mysql-connector-j")

    //lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // 测试依赖
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    //加密工具类
    implementation("org.mindrot:jbcrypt:0.4")
    //rss依赖
    implementation("com.rometools:rome:2.1.0")
    implementation("com.rometools:rome-modules:2.1.0")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
