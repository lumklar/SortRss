package buildlogic.release

import buildlogic.flavors.StringEnum
import buildlogic.utils.gradlewPath
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider
import java.io.File

/**
 * 注册一个“发布产物”的 Gradle 任务。
 *
 * 此函数在配置阶段会捕获所有需要的信息（路径、模块等），
 * 并将它们作为任务的输入属性，从而避免在执行阶段访问 Project，满足配置缓存要求。
 *
 * 不同 CPU 架构下 jpackage 产物名可能不同（deb 用 amd64/arm64、rpm 用 x86_64/aarch64、
 * exe/msi 可能带架构后缀），统一由 artifactRelativePath 的通配符吸收，
 * 输出名则由 [renameTo] 规范化为 `<artifactName>-<arch>-<flavor>-<version>`。
 *
 * @param taskName 注册的任务名称
 * @param moduleName 目标模块的名称（如 ":app"），必须在当前构建中存在
 * @param moduleTask 目标模块中需要执行的任务名（如 "build"）
 * @param artifactRelativePath 产物相对目标模块 build 目录的路径（如 "libs/app.jar"）
 * @param compression 压缩方式
 * @param renameTo 重命名（不含后缀，打包时自动加 .tar.gz）
 * @param envVars 需要传递给子任务的环境变量列表（实现 StringEnum）
 */
private fun Project.registerReleaseTask(
    taskName: String,
    moduleName: String,
    moduleTask: String,
    artifactRelativePath: String,
    compression: CompressionMode = CompressionMode.NONE,
    renameTo: String? = null,
    envVars: List<StringEnum> = emptyList()
): TaskProvider<ReleasePublishTask> {
    // 获取目标模块的 Project 对象（配置阶段即可访问）
    val targetProject = project(moduleName)

    // 确定 gradlew 路径（根项目下）
    val targetBuildDir = targetProject.layout.buildDirectory.get().asFile
    val destinationDir = project.layout.buildDirectory.dir("release").get().asFile

    // 注册任务
    return tasks.register(taskName, ReleasePublishTask::class.java) {
        group = "release"
        description = "发布产物（$moduleName:$moduleTask）到 build/release"

        // 将所有信息设置为任务属性（配置缓存安全）
        this.moduleTask.set(moduleTask)
        this.gradlewPath.set(gradlewPath())
        this.targetProjectDir.set(targetProject.projectDir)
        this.targetBuildDir.set(targetBuildDir)
        this.artifactRelativePath.set(artifactRelativePath)
        this.renameTo.set(renameTo)
        this.compression.set(compression)
        this.envVars.set(envVars)
        // 输出目录固定为当前项目的 build/release
        this.destinationDir.set(destinationDir)
    }
}

/**

 * 批量注册发布任务。
 *
 * 根据传入的配置列表，依次为每个配置中的每个环境变量组合生成一个发布任务，
 * 任务命名遵循 `release-<target>-<envSuffix>` 格式，产物命名遵循
 * `sortrss-<artifactName>[-<arch>]-<envSuffix>-<version>` 格式。
 * 同时为每个 group 创建聚合任务 `release-<group>`，依赖该组所有子任务；
 * 最后创建 `release-all` 任务，依赖所有 group 聚合任务。
 *
 * 版本号自动从 `project.version` 获取，用于产物重命名。
 *
 * @param configs 发布配置列表
 */
fun Project.registerReleaseTasks(configs: List<ReleaseConfig>) {
    // 获取当前 CPU 架构，并规范化为常见地命名形式
    val arch = System.getProperty("os.arch").let { raw ->
        when (raw) {
            "x86_64", "amd64" -> "x64"
            "aarch64", "arm64" -> "arm64"
            else -> raw // 保留原样，建议后续按需补充映射
        }
    }

    // 用于收集每个 group 下的所有任务（Task 对象）
    val groupTasksMap = mutableMapOf<String, MutableList<Task>>()
    // 存储每个模块下的任务列表（Task 对象）
    val moduleTasksMap = mutableMapOf<String, MutableList<Task>>()
    // 任务名与产物名都必须唯一，否则会互相覆盖。
    // 注意：group 允许重复（wasmjs/js 同属 web），聚合任务按 group 去重后注册一次。
    val seenTaskNames = mutableSetOf<String>()
    val seenArtifactNames = mutableSetOf<String>()

    configs.forEach { config ->
        val group = config.group
        val artifactName = config.effectiveArtifactName

        // 任务名/产物名在不同风味下仍需唯一；用不含 env 的主键检查即可发现重复声明
        check(seenTaskNames.add(config.target)) {
            "重复的 release target '${config.target}'，任务名会冲突"
        }
        check(seenArtifactNames.add(artifactName)) {
            "重复的 release artifactName '$artifactName'（target='${config.target}'），产物会互相覆盖"
        }

        config.envVarsCombinations.forEach { envVarsList ->
            // 生成环境变量后缀（用 "-" 连接所有 value）
            val envSuffix = if (envVarsList.isNotEmpty()) "-${envVarsList.joinToString("-") { it.value }}" else ""

            // 任务名称：release-<target>-<envSuffix>
            val taskName = "${ReleaseNames.TASK_PREFIX}-${config.target}$envSuffix"

            // 从项目获取版本号，作为重命名的一部分
            val version = project.version.toString()

            // 构造基础名称
            // 若架构无关，则不添加 arch 段；否则添加 -<arch>
            val archSuffix = if (config.architectureIndependent) "" else "-$arch"
            val baseName = "${ReleaseNames.ARTIFACT_PREFIX}-$artifactName$archSuffix$envSuffix-$version"

            // 调用原有的注册函数
            registerReleaseTask(
                taskName = taskName,
                moduleName = config.moduleName,
                moduleTask = config.moduleTask,
                artifactRelativePath = config.artifactRelativePath,
                compression = config.compression,
                renameTo = baseName,
                envVars = envVarsList
            )

            // 将刚注册的任务加入分组映射
            val task = tasks.getByName(taskName)
            groupTasksMap.getOrPut(group) { mutableListOf() }.add(task)

            // 按模块名分组（用于串行约束）
            moduleTasksMap.getOrPut(config.moduleName) { mutableListOf() }.add(task)
        }
    }

    // 任务图级串行约束：同一模块的多个任务必须按顺序调度。
    // 与 ReleasePublishTask 内的跨进程文件锁是互补的两层保护——
    // mustRunAfter 保证单进程内任务不并发调度，文件锁保证跨进程/跨构建不争抢 build 目录。
    moduleTasksMap.values.forEach { taskList ->
        if (taskList.size > 1) {
            // 按任务名称排序，确保顺序稳定
            val sorted = taskList.sortedBy { it.name }
            for (i in 1 until sorted.size) {
                // 后一个任务必须在前一个任务之后执行
                sorted[i].mustRunAfter(sorted[i - 1])
            }
        }
    }

    // 为每个 group 创建聚合任务
    groupTasksMap.forEach { (groupName, taskList) ->
        tasks.register("${ReleaseNames.TASK_PREFIX}-$groupName") {
            group = "release-group"
            description = "发布所有 $groupName 产物"
            dependsOn(taskList)
        }
    }

    // 创建总任务 release-all，依赖所有 group 聚合任务
    tasks.register("${ReleaseNames.TASK_PREFIX}-all") {
        group = "release-group"
        description = "发布所有组产物"
        dependsOn(groupTasksMap.keys.map { "${ReleaseNames.TASK_PREFIX}-$it" })
    }
}
