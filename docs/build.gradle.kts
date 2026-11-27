import buildlogic.constant.PropertiesContant
import buildlogic.docs.MikeDeployConfig
import buildlogic.docs.createMikeDeployTask
import buildlogic.utils.getConfigString
import ru.vyarus.gradle.plugin.mkdocs.task.MkdocsBuildTask

plugins {
    id("base")
    alias(libs.plugins.mkdocs.build)
//    alias(libs.plugins.mkdocs)
    // use-python 插件提供 Python 环境，installMikeDeps 按 requirements.txt 装 mkdocs/mike；
    // CI 侧用 actions/setup-python。Chaquopy 是 Android 内嵌 CPython 的插件，与文档构建无关。
    alias(libs.plugins.use.python)
}

val repoUrl = getConfigString(PropertiesContant.REPO_URL, "https://github.com/lumklar/SortRss")
val siteUrl = getConfigString(PropertiesContant.SITE_URL, "https://lumklar.github.io/SortRss")

//mkdocs {
//    extras = mapOf(
//        "REPO_URL" to repoUrl,
//        "SITE_URL" to siteUrl + "/docs/"
//    )
//}

// 为 mkdocsBuild 任务设置环境变量
tasks.named<MkdocsBuildTask>("mkdocsBuild") {
    environment("REPO_URL", repoUrl)
    environment("SITE_URL", siteUrl + "/docs/")
    environment("VERSION", project.version.toString())
}

// 注册一个 Sync 任务，将 mkdocs 产物从 build/mkdocs 移动到 build/dist
// 不做 HTML 压缩：mkdocs-material 产物已足够精简，压缩收益小且会破坏可读性；
// 站点体积优化应交给部署层的 gzip/brotli，而非在构建产物阶段改写 HTML。
val moveDocs = tasks.register<Sync>("moveDocs") {
    dependsOn("mkdocsBuild")           // 确保先构建
    from("build/mkdocs")               // 源目录
    into("build/dist")                 // 目标目录
}

val mikeTasks = createMikeDeployTask(
    MikeDeployConfig(
        workingDir = "src/doc",
        branch = "docs",
        deployPrefix = "docs/",
        versions = listOf(project.version.toString()),
        defaultVersion = project.version.toString()
    )
)

// 获取关键任务引用
val unpackTask = mikeTasks.unpack                 // 单独的 GitArchiveUnpackTask

// 新增：将 mike 解压产物复制到 build/dist
val moveMikeDocs = tasks.register<Sync>("moveMikeDocs") {
    dependsOn(unpackTask)                    // 确保解压完成
    from("build/mike")     // 源目录：GitArchiveUnpackTask 的 unpackDir 输出
    into("build/dist")                            // 目标目录
}

// 挂接到 assemble
tasks.named("assemble") {
    dependsOn(moveDocs)
}
