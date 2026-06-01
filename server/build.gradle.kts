plugins {
    kotlin("jvm") version "2.3.21"
    kotlin("plugin.spring") version "2.3.21"
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
    metadataRepository {
        enabled = true
    }
    binaries {
        named("main") {
            buildArgs.add("-O2")
//            buildArgs.add("--enable-all-security-services")
        }
    }
}

dependencies {
    implementation(project(":common"))

    //web
    implementation("org.springframework.boot:spring-boot-starter-web")
    //缓存
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("com.github.ben-manes.caffeine:caffeine")
    //数据库
    implementation("jakarta.persistence:jakarta.persistence-api")
//    implementation("com.baomidou:mybatis-plus-spring-boot4-starter:3.5.16")
//    implementation("com.baomidou:mybatis-plus-jsqlparser:3.5.16")
//    implementation("org.mybatis.spring.boot:mybatis-spring-boot-starter:4.0.1")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("com.mysql:mysql-connector-j")

    //lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // 测试依赖
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
