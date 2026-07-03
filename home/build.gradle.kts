import org.codehaus.groovy.ast.tools.GeneralUtils.args
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.tasks.Sync
import org.gradle.internal.logging.progress.ResourceOperation

plugins {
    id("base")
    id("com.github.node-gradle.node") version "7.1.0"
}

// -------- Node 配置 ----------
node {
    version = "20.0.0"          // 直接赋值
    npmVersion = "10.5.0"       // 直接赋值
}

// -------- 压缩前端资源 ----------
tasks.register<com.github.gradle.node.npm.task.NpmTask>("compressFrontend") {
    dependsOn("npmInstall")           // 使用插件提供的 npmInstall
    args.set(listOf("run", "compress")) // args 是 ListProperty，set() 方法可用
    inputs.dir("src")
    outputs.dir(layout.buildDirectory.dir("compressed"))
}

// -------- 准备分发目录 ----------
val prepareDistribution = tasks.register<Sync>("prepareDistribution") {
    dependsOn("compressFrontend")
    dependsOn(project(":app:webApp").tasks.named("wasmJsBrowserDistribution"))

    val distDir = layout.buildDirectory.dir("dist").get().asFile
    into(distDir)

    // demo 产物
    from(project(":app:webApp").layout.buildDirectory.dir("dist/wasmJs/productionExecutable")) {
        into("demo")
        include("**/*")
    }

    // 压缩后的前端资源
    from(layout.buildDirectory.dir("compressed")) {
        include("**/*")
    }

    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

// 挂接到 assemble
tasks.named("assemble") {
    dependsOn(prepareDistribution)
}