import buildlogic.constant.PropertiesContant
import buildlogic.utils.getConfigString
import ru.vyarus.gradle.plugin.mkdocs.task.MkdocsBuildTask

plugins {
    id("base")
    alias(libs.plugins.mkdocs.build)
//    alias(libs.plugins.mkdocs)
    alias(libs.plugins.use.python)
    //TODO 需要Chaquopy等插件来自动安装python吗？
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
}

// 注册一个 Sync 任务，将 mkdocs 产物从 build/mkdocs 移动到 build/dist
val moveDocs = tasks.register<Sync>("moveDocs") {
    //TODO 增加多版本文档
    //TODO 是否压缩html？
    dependsOn("mkdocsBuild")           // 确保先构建
    from("build/mkdocs")               // 源目录
    into("build/dist")                 // 目标目录
}

// 挂接到 assemble
tasks.named("assemble") {
    dependsOn(moveDocs)
}
