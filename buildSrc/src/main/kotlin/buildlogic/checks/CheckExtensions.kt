package buildlogic.checks

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider
import java.io.File

// 任务名称常量，统一管理
const val CHECK_MODULE_NAMES = "checkModuleNames"
const val CHECK_DEPENDENCY_VERSIONS = "checkDependencyVersions"
const val CHECK_PLUGIN_VERSIONS = "checkPluginVersions"
const val CHECK_CONFIG_KEYS = "checkConfigKeys"
const val CHECK_VERSION_OVERRIDES = "checkVersionOverrides"

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
 * 注册配置键检查任务
 */
fun Project.registerConfigKeyCheck(): TaskProvider<CheckConfigKeysTask> {
    val constantDir = rootProject.file("buildSrc/src/main/kotlin/buildlogic/constant")
    return tasks.register(CHECK_CONFIG_KEYS, CheckConfigKeysTask::class.java) {
        propertiesFile.set(File(constantDir, "PropertiesContant.kt"))
        envFile.set(File(constantDir, "EnvConstant.kt"))
    }
}

/**
 * 注册临时版本覆盖检查任务。
 * @param overrides bom 属性名 -> (版本目录别名, 覆盖版本)
 */
fun Project.registerVersionOverrideCheck(
    overridesFile: File,
    versionCatalogFile: File,
    overrides: Map<String, Pair<String, String>>,
    springBootVersion: String
): TaskProvider<CheckVersionOverridesTask> {
    val bomNotation = "org.springframework.boot:spring-boot-dependencies:$springBootVersion@pom"
    val bomFiles = configurations.detachedConfiguration(dependencies.create(bomNotation))
    return tasks.register(CHECK_VERSION_OVERRIDES, CheckVersionOverridesTask::class.java) {
        this.overridesFile.set(overridesFile)
        this.versionCatalogFile.set(versionCatalogFile)
        overrideAliases.set(overrides.mapValues { (_, aliasAndVersion) -> aliasAndVersion.first })
        overrideVersions.set(overrides.mapValues { (_, aliasAndVersion) -> aliasAndVersion.second })
        this.springBootVersion.set(springBootVersion)
        bomPom.from(bomFiles)
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
 * 同时注册四个常规检查任务（临时版本覆盖检查由 gradle/version-overrides.gradle.kts 单独注册）
 */
fun Project.registerAllChecks(
    autoAttachToCheck: Boolean = true
): List<Pair<String, TaskProvider<out Task>>> {
    val moduleProvider = registerModuleNameCheck()
    val depProvider = registerDependencyVersionCheck()
    val pluginProvider = registerPluginVersionCheck()
    val configKeyProvider = registerConfigKeyCheck()

    if (autoAttachToCheck) {
        tasks.findByName("check")?.dependsOn(moduleProvider, depProvider, pluginProvider, configKeyProvider)
            ?: logger.warn(
                "Task 'check' not found, skipping dependency attachment. " +
                        "(Did you apply the 'base' plugin?)"
            )
    }

    return listOf(
        CHECK_MODULE_NAMES to moduleProvider,
        CHECK_DEPENDENCY_VERSIONS to depProvider,
        CHECK_PLUGIN_VERSIONS to pluginProvider,
        CHECK_CONFIG_KEYS to configKeyProvider
    )
}
