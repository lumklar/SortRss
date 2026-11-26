import buildlogic.constant.PropertiesContant
import buildlogic.utils.getConfigString
import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    id("base")
    alias(libs.plugins.node)
}

// -------- Node 配置 ----------
node {
    // node-gradle 7.x 的 download 默认为 false（直接使用 PATH 里的系统 Node），
    // 那样 version/npmVersion 配置不会生效。显式打开下载，确保构建使用下面固定的
    // Node/npm 版本，而不是开发者机器上偶然安装的 Node。
    download = true
    version = "24.22.3"          // 直接赋值（Node 24 LTS Krypton，含安全修复）
    npmVersion = "11.19.1"      // 直接赋值（随 Node 24.22.3 捆绑）
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