package buildlogic.release

import buildlogic.flavors.StringEnum
import org.gradle.api.Project

/**
 * 注册一个“发布产物”的 Gradle 任务。
 *
 * 此函数在配置阶段会捕获所有需要的信息（路径、模块等），
 * 并将它们作为任务的输入属性，从而避免在执行阶段访问 Project，满足配置缓存要求。
 *
 * @param taskName 注册的任务名称
 * @param moduleName 目标模块的名称（如 ":app"），必须在当前构建中存在
 * @param moduleTask 目标模块中需要执行的任务名（如 "build"）
 * @param artifactRelativePath 产物相对目标模块 build 目录的路径（如 "libs/app.jar"）
 * @param shouldPackage 是否打包为 tar.gz（true 打包，false 仅复制）
 * @param renameTo 重命名（不含后缀，打包时自动加 .tar.gz）
 * @param envVars 需要传递给子任务的环境变量列表（实现 StringEnum）
 */
fun Project.registerReleaseTask(
    taskName: String,
    moduleName: String,
    moduleTask: String,
    artifactRelativePath: String,
    shouldPackage: Boolean = false,
    renameTo: String? = null,
    envVars: List<StringEnum> = emptyList()
) {
    // 获取目标模块的 Project 对象（配置阶段即可访问）
    val targetProject = project(moduleName)

    // 确定 gradlew 路径（根项目下）
    val gradlew = if (System.getProperty("os.name").startsWith("Windows")) "gradlew.bat" else "gradlew"
    val gradlewPath = rootProject.projectDir.resolve(gradlew).absolutePath
    val targetBuildDir = targetProject.layout.buildDirectory.get().asFile
    val destinationDir = project.layout.buildDirectory.dir("release").get().asFile

    // 注册任务
    tasks.register(taskName, ReleasePublishTask::class.java) {
        group = "release"
        description = "发布产物（$moduleName:$moduleTask）到 build/release"

        // 将所有信息设置为任务属性（配置缓存安全）
        this.moduleTask.set(moduleTask)
        this.gradlewPath.set(gradlewPath)
        this.targetProjectDir.set(targetProject.projectDir)
        this.targetBuildDir.set(targetBuildDir )
        this.artifactRelativePath.set(artifactRelativePath)
        this.renameTo.set(renameTo)
        this.shouldPackage.set(shouldPackage)
        this.envVars.set(envVars)
        // 输出目录固定为当前项目的 build/release
        this.destinationDir.set(destinationDir)
    }
}