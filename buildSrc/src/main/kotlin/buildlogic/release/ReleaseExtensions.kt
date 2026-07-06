package buildlogic.release

import buildlogic.flavors.StringEnum
import org.gradle.api.Project
import org.gradle.api.Task

/**
 * 注册一个“发布产物”的 Gradle 任务。
 * TODO 支持不同cpu架构产物名称不一致
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
private fun Project.registerReleaseTask(
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
        this.targetBuildDir.set(targetBuildDir)
        this.artifactRelativePath.set(artifactRelativePath)
        this.renameTo.set(renameTo)
        this.shouldPackage.set(shouldPackage)
        this.envVars.set(envVars)
        // 输出目录固定为当前项目的 build/release
        this.destinationDir.set(destinationDir)
    }
}

/**

 * 批量注册发布任务。
 *
 * 根据传入的配置列表，依次为每个配置中的每个环境变量组合生成一个发布任务，
 * 任务命名遵循 `release-<target>-<envSuffix>` 格式。
 * 同时为每个 group 创建聚合任务 `release-<group>`，依赖该组所有子任务；
 * 最后创建 `release-all` 任务，依赖所有 group 聚合任务。
 *
 * 版本号自动从 `project.version` 获取，用于产物重命名。
 *
 * @param configs 发布配置列表
 */
fun Project.registerReleaseTasks(configs: List<ReleaseConfig>) {
    // 获取当前 CPU 架构，并规范化为常见的命名形式
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

    configs.forEach { config ->
        val group = config.group
        config.envVarsCombinations.forEach { envVarsList ->
            // 生成环境变量后缀（用 "-" 连接所有 value）
            val envSuffix = if (envVarsList.isNotEmpty()) "-${envVarsList.joinToString("-") { it.value }}" else ""

            // 任务名称：release-<target>-<envSuffix>
            val taskName = "release-${config.target}$envSuffix"

            // 从项目获取版本号，作为重命名的一部分
            val version = project.version.toString()

            // 构造基础名称
            // 若架构无关，则不添加 arch 段；否则添加 -<arch>
            val archSuffix = if (config.architectureIndependent) "" else "-$arch"
            val baseName = "sortrss-${config.target}$archSuffix$envSuffix-$version"

            // 构造 renameTo（遵循原有约定：打包时不含后缀，非打包时包含扩展名）
            val renameTo = if (config.shouldPackage) {
                baseName // 不含后缀，ReleasePublishTask 会自动添加 .tar.gz
            } else {
                // 提取 artifactRelativePath 的扩展名（如 ".jar"）
                val ext = config.artifactRelativePath.substringAfterLast('.', "")
                if (ext.isNotEmpty()) "$baseName.$ext" else baseName
            }

            // 调用原有的注册函数
            registerReleaseTask(
                taskName = taskName,
                moduleName = config.moduleName,
                moduleTask = config.moduleTask,
                artifactRelativePath = config.artifactRelativePath,
                shouldPackage = config.shouldPackage,
                renameTo = renameTo,
                envVars = envVarsList
            )

            // 将刚注册的任务加入分组映射
            val task = tasks.getByName(taskName)
            groupTasksMap.getOrPut(group) { mutableListOf() }.add(task)

            // 按模块名分组（用于串行约束）
            moduleTasksMap.getOrPut(config.moduleName) { mutableListOf() }.add(task)
        }
    }

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
        tasks.register("release-$groupName") {
            group = "release-group"
            description = "发布所有 $groupName 产物"
            dependsOn(taskList)
        }
    }

    // 创建总任务 release-all，依赖所有 group 聚合任务
    tasks.register("release-all") {
        group = "release-group"
        description = "发布所有组产物"
        dependsOn(groupTasksMap.keys.map { "release-$it" })
    }
}