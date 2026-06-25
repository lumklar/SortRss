package buildlogic.docker

import buildlogic.constant.EnvConstant
import buildlogic.constant.PropertiesContant
import buildlogic.utils.getConfigString
import org.gradle.api.Project
import org.gradle.api.tasks.Exec

/**
 * 注册一个执行 docker build 的任务
 * @param taskName 任务名
 * @param dockerFileDir Dockerfile 所在目录
 * @param buildArgs 构建参数
 * @param localTags 本地构建使用的标签列表（单平台），第一个作为主标签
 * @param multiPlatformPushTags 多平台构建时使用的标签列表（带 Registry），将直接推送
 * @param dependencies 前置任务
 */
fun Project.createDockerBuildTask(
    taskName: String,
    dockerFileDir: String,
    buildArgs: Map<String, String>,
    localTags: List<String>,
    multiPlatformPushTags: List<String> = emptyList(),
    vararg dependencies: String
) {
    tasks.register(taskName, Exec::class.java) {
        group = "docker"
        description = "Build Docker image using $dockerFileDir/Dockerfile"
        dependsOn(dependencies.toList())

        val dockerPlatforms = project.getConfigString(EnvConstant.DOCKER_PLATFORMS)
        val isMultiPlatform = !dockerPlatforms.isNullOrBlank()

        val cmd = mutableListOf<String>()
        if (isMultiPlatform && multiPlatformPushTags.isNotEmpty()) {
            cmd.add("docker")
            cmd.add("buildx")
            cmd.add("build")
            cmd.addAll(listOf("--platform", dockerPlatforms!!))
            // 多平台直接推送所有标签
            multiPlatformPushTags.forEach { tag -> cmd.addAll(listOf("--tag", tag)) }
            cmd.add("--push")
        } else {
            cmd.add("docker")
            cmd.add("build")
            localTags.forEach { tag -> cmd.addAll(listOf("--tag", tag)) }
        }

        cmd.addAll(listOf("--file", "$dockerFileDir/Dockerfile"))
        buildArgs.forEach { (key, value) ->
            cmd.addAll(listOf("--build-arg", "$key=$value"))
        }
        cmd.add(".")

        commandLine(cmd)
        doFirst {
            println("Executing: ${commandLine.joinToString(" ")}")
        }
    }
}

// ==================== 便捷构建方法 ====================

/**
 * 创建 Docker 构建任务（自动解析标签）
 */
fun Project.createDockerTask(
    dockerFileRelativePath: String,
    tagSuffix: String,
    taskSuffix: String = tagSuffix,
    imageTagPrefix: String,          // 形如 "lumklar/sortrss:1.0-"  （无 Registry）
    buildArgs: Map<String, String> = emptyMap(),
    tagAsLatestVariant: Boolean = true,
    tagAsGlobalLatest: Boolean = false,
    vararg dependencies: String
) {
    // 读取并解析 Registry 列表
    val rawRegistries = getConfigString(PropertiesContant.DOCKER_REGISTRY, "")
    val registries = rawRegistries.split(",").map { it.trim() }.filter { it.isNotEmpty() }

    // 从 imageTagPrefix 拆分出 namespace/imageName 和 version
    val prefixBeforeColon = imageTagPrefix.substringBefore(":")
    val version = imageTagPrefix.substringAfter(":").removeSuffix("-")
    val parts = prefixBeforeColon.split("/")
    require(parts.size == 2) { "imageTagPrefix must be in format 'namespace/imageName:version-'" }
    val namespace = parts[0]
    val imageName = parts[1]

    val imageTags = resolveImageTags(
        namespace = namespace,
        imageName = imageName,
        version = version,
        suffix = getConfigString(PropertiesContant.DOCKER_TAG_SUFFIX, tagSuffix),
        tagAsLatestVariant = tagAsLatestVariant,
        tagAsGlobalLatest = tagAsGlobalLatest,
        registries = registries
    )

    val taskName = "buildDockerImage-$taskSuffix"
    val dockerFileDir = "deploy/docker/$dockerFileRelativePath"

    createDockerBuildTask(
        taskName = taskName,
        dockerFileDir = dockerFileDir,
        buildArgs = mapOf("VERSION" to version) + buildArgs,
        localTags = imageTags.buildTags,
        multiPlatformPushTags = imageTags.pushTags,
        dependencies = dependencies
    )
}

// 简化重载（suffix 与 taskSuffix 相同）
fun Project.createDockerTask(
    dockerFileRelativePath: String,
    suffix: String,
    imageTagPrefix: String,
    buildArgs: Map<String, String> = emptyMap(),
    tagAsLatestVariant: Boolean = true,
    tagAsGlobalLatest: Boolean = false,
    vararg dependencies: String
) {
    createDockerTask(
        dockerFileRelativePath = dockerFileRelativePath,
        tagSuffix = suffix,
        taskSuffix = suffix,
        imageTagPrefix = imageTagPrefix,
        buildArgs = buildArgs,
        tagAsLatestVariant = tagAsLatestVariant,
        tagAsGlobalLatest = tagAsGlobalLatest,
        dependencies = dependencies
    )
}

/**
 * TODO 考虑隐藏对外暴露的方法，imageTagPrefix不传入？内部构造？
 * 创建 latest 变体任务（版本标签 + latest-<suffix>）
 */
fun Project.createLatestDockerTask(
    dockerFileRelativePath: String,
    suffix: String,
    imageTagPrefix: String,
    buildArgs: Map<String, String> = emptyMap(),
    tagAsGlobalLatest: Boolean = false,
    vararg dependencies: String
) {
    createDockerTask(
        dockerFileRelativePath = dockerFileRelativePath,
        taskSuffix = "$suffix-latest",
        tagSuffix = suffix,
        imageTagPrefix = imageTagPrefix,
        buildArgs = buildArgs,
        tagAsLatestVariant = true,
        tagAsGlobalLatest = tagAsGlobalLatest,
        dependencies = dependencies
    )
}

/**
 * 同时创建两个任务：普通版本（仅版本标签） + latest 变体
 */
fun Project.createDockerTasks(
    dockerFileRelativePath: String,
    suffix: String,
    imageTagPrefix: String,
    buildArgs: Map<String, String> = emptyMap(),
    tagAsGlobalLatest: Boolean = false,
    vararg dependencies: String
) {
    createDockerTask(
        dockerFileRelativePath = dockerFileRelativePath,
        suffix = suffix,
        imageTagPrefix = imageTagPrefix,
        buildArgs = buildArgs,
        tagAsLatestVariant = false,
        tagAsGlobalLatest = tagAsGlobalLatest,
        dependencies = dependencies
    )
    createLatestDockerTask(
        dockerFileRelativePath = dockerFileRelativePath,
        suffix = suffix,
        imageTagPrefix = imageTagPrefix,
        buildArgs = buildArgs,
        tagAsGlobalLatest = tagAsGlobalLatest,
        dependencies = dependencies
    )
}