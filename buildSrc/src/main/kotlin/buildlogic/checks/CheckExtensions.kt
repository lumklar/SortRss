package buildlogic.checks

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider

// 任务名称常量，统一管理
const val CHECK_MODULE_NAMES = "checkModuleNames"
const val CHECK_DEPENDENCY_VERSIONS = "checkDependencyVersions"
const val CHECK_PLUGIN_VERSIONS = "checkPluginVersions"

/**
 * 默认的依赖配置名称列表
 */
private fun defaultDependencyConfigs() = setOf(
    "implementation", "api", "compileOnly", "runtimeOnly",
    "testImplementation", "testApi", "testCompileOnly", "testRuntimeOnly",
    "androidTestImplementation", "androidTestApi",
    "kapt", "ksp", "annotationProcessor",
    "debugImplementation", "releaseImplementation"
)

/**
 * 注册依赖版本检查任务
 */
fun Project.registerDependencyVersionCheck(
    dependencyConfigs: Set<String> = defaultDependencyConfigs()
): TaskProvider<CheckDependencyVersionsTask> {
    val rootDir = rootDir
    val buildFiles = fileTree(rootDir) {
        include("**/build.gradle", "**/build.gradle.kts")
        exclude("**/build/**")
    }
    val buildFileContents = buildFiles.associate { file ->
        file.relativeTo(rootDir).path to file.readText()
    }

    return tasks.register(CHECK_DEPENDENCY_VERSIONS, CheckDependencyVersionsTask::class.java) {
        setBuildFileContents(buildFileContents)
        setDependencyConfigs(dependencyConfigs)
        group = "verification"
        description = "检查所有依赖是否通过 libs.versions.toml 管理"
    }
}

/**
 * 注册插件版本检查任务
 */
fun Project.registerPluginVersionCheck(): TaskProvider<CheckPluginVersionsTask> {
    val rootDir = rootDir
    val buildFiles = fileTree(rootDir) {
        include("**/build.gradle", "**/build.gradle.kts")
        exclude("**/build/**")
    }
    val buildFileContents = buildFiles.associate { file ->
        file.relativeTo(rootDir).path to file.readText()
    }

    return tasks.register(CHECK_PLUGIN_VERSIONS, CheckPluginVersionsTask::class.java) {
        setBuildFileContents(buildFileContents)
        group = "verification"
        description = "检查所有 build.gradle(.kts) 中的插件版本是否通过 libs.versions.toml 管理"
    }
}

/**
 * 注册模块名检查任务
 */
fun Project.registerModuleNameCheck(): TaskProvider<CheckModuleNamesTask> {
    val moduleNameToPaths = mutableMapOf<String, MutableList<String>>()
    rootProject.subprojects.forEach { sub ->
        val hasBuildFile = sub.buildFile.exists() || sub.file("build.gradle").exists()
        if (hasBuildFile) {
            moduleNameToPaths.getOrPut(sub.name) { mutableListOf() }.add(sub.path)
        }
    }

    return tasks.register(CHECK_MODULE_NAMES, CheckModuleNamesTask::class.java) {
        setModuleNameToPaths(moduleNameToPaths)
        group = "verification"
        description = "检查所有子模块名称是否唯一"
    }
}

/**
 * 同时注册三个检查任务
 */
fun Project.registerAllChecks(
    autoAttachToCheck: Boolean = true
): List<Pair<String, TaskProvider<out Task>>> {
    val moduleProvider = registerModuleNameCheck()
    val depProvider = registerDependencyVersionCheck()
    val pluginProvider = registerPluginVersionCheck() // 新增

    if (autoAttachToCheck) {
        tasks.findByName("check")?.dependsOn(moduleProvider, depProvider, pluginProvider)
            ?: logger.warn(
                "Task 'check' not found, skipping dependency attachment. " +
                        "(Did you apply the 'base' plugin?)"
            )
    }

    return listOf(
        CHECK_MODULE_NAMES to moduleProvider,
        CHECK_DEPENDENCY_VERSIONS to depProvider,
        CHECK_PLUGIN_VERSIONS to pluginProvider
    )
}
