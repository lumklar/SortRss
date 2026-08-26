import buildlogic.constant.PropertiesContant
import buildlogic.utils.getConfigString
import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    id("base")
    alias(libs.plugins.node)
}

// -------- Node 配置 ----------
node {
    version = "24.13.0"          // 直接赋值
    npmVersion = "10.5.0"       // 直接赋值
}

// 注册转换任务
tasks.register<com.github.gradle.node.npm.task.NpmTask>("convertFavicon") {
    dependsOn("npmInstall")               // 确保依赖已安装
    args.set(listOf("run", "convert-favicon"))
    inputs.file("src/favicon.svg")         // 增量构建：当 SVG 变化时才执行
    outputs.file(layout.buildDirectory.file("dist/favicon.ico")) // 输出位置
}

// -------- 压缩前端资源 ----------
tasks.register<com.github.gradle.node.npm.task.NpmTask>("compressFrontend") {
    dependsOn("npmInstall")           // 使用插件提供的 npmInstall
    args.set(listOf("run", "compress")) // args 是 ListProperty，set() 方法可用
    inputs.dir("src")
    outputs.dir(layout.buildDirectory.dir("compressed"))
}

// -------- 公共函数：创建分发任务 ----------
fun Project.createDistributionTask(taskName: String, docsTaskName: String): TaskProvider<Sync> {
    return tasks.register<Sync>(taskName) {
        dependsOn(
            "compressFrontend",
            "convertFavicon",
            project(":docs").tasks.named(docsTaskName),
            project(":app:webApp").tasks.named("wasmJsBrowserDistribution")
        )

        into(layout.buildDirectory.dir("dist"))

        // docs 产物
        from(project(":docs").layout.buildDirectory.dir("dist")) {
            into("docs")
            include("**/*")
        }

        // demo 产物
        from(project(":app:webApp").layout.buildDirectory.dir("dist/wasmJs/productionExecutable")) {
            into("demo")
            include("**/*")
        }

        val repoUrl = getConfigString(PropertiesContant.REPO_URL, "https://github.com/lumklar/SortRss")
        inputs.property("repoUrl", repoUrl)

        // 压缩后的前端资源
        from(layout.buildDirectory.dir("compressed")) {
            include("**/*")
            filesMatching("index.html") {
                filter<ReplaceTokens>(
                    "tokens" to mapOf(
                        "REPO_URL" to repoUrl
                    )
                )
            }
        }

        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }
}

// -------- 注册两个任务，共用逻辑 ----------
val prepareDistribution = createDistributionTask("prepareDistribution", "moveDocs")
val buildWithMike = createDistributionTask("prepareDistributionWithMike", "moveMikeDocs")

// 挂接到 assemble
tasks.named("assemble") {
    dependsOn(prepareDistribution)
}