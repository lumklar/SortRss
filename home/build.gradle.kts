// 应用 base 插件，提供 assemble、clean 等标准生命周期任务
plugins {
    id("base")
}

// 使用 Sync 任务替代 Copy，自动清理目标目录中多余的旧文件，无需手动 deleteRecursively
val prepareDistribution = tasks.register<Sync>("prepareDistribution") {
    // 依赖 :app:webApp 的构建任务
    dependsOn(project(":app:webApp").tasks.named("wasmJsBrowserDistribution"))

    // 目标目录：build/dist
    val distDir = layout.buildDirectory.dir("dist").get().asFile
    into(distDir)

    // 方式一：复制 home/src 全部内容到 dist 根目录
    // 使用 project.projectDir.resolve("src") 并指定 include，确保目录结构
    from(project.projectDir.resolve("src")) {
        include("**/*")
    }

    // 方式二：复制 demo 产物到 dist/demo
    // 通过 provider 延迟获取源目录，避免配置时直接调用 .get() 引发警告
    val demoSourceProvider = project(":app:webApp").layout.buildDirectory.dir("dist/wasmJs/productionExecutable")
    from(demoSourceProvider) {
        into("demo")
        include("**/*")
    }

    // 重复文件处理策略
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

// 将 prepareDistribution 挂接到 assemble 生命周期
// 使用 tasks.named 安全地获取现有任务，如果 assemble 不存在（但已应用 base，肯定存在）
tasks.named("assemble") {
    dependsOn(prepareDistribution)
}