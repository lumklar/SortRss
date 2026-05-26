// 全局项目信息
allprojects {
    group = "com.github.lumklar"
    version = "0.0.1-SNAPSHOT"
    description = "Sort-Rss multi-module project"

    // 所有模块共用仓库
    repositories {
        mavenCentral()
        google()
    }
}

// 子项目配置
subprojects {
    // 给所有子项目添加缺失的任务（终极保险）
    tasks.register("prepareKotlinBuildScriptModel") {}
}
