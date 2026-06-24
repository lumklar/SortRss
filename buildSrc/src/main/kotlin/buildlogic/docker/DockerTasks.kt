package buildlogic.docker

import buildlogic.flavors.DataFlavor
import org.gradle.api.Project
import org.gradle.api.tasks.Exec

// ========== 数据类 ==========
data class FlavorCombination(
    val data: DataFlavor,
)

data class DockerTaskConfig(
    val dockerFileRelativePath: String,
    val suffix: String,
    val buildArgs: Map<String, String> = emptyMap(),
    val tagAsGlobalLatest: Boolean = false,
    val dependencies: List<String> = emptyList(),
    val flavors: FlavorCombination? = null
)

// ========== 扩展函数（都在 Project 上） ==========

/**
 * 底层 Docker 构建任务注册
 */
fun Project.createDockerBuildTask(
    taskName: String,
    dockerFileDir: String,
    buildArgs: Map<String, String>,
    imageTag: String,
    extraTags: List<String> = emptyList(),
    vararg dependencies: String
) {
    tasks.register(taskName, Exec::class.java) {
        group = "docker"
        description = "Build Docker image using $dockerFileDir/Dockerfile"
        dependsOn(dependencies.toList())

        val cmd = mutableListOf(
            "docker", "build",
            "--file", "$dockerFileDir/Dockerfile",
            "--tag", imageTag
        )
        extraTags.forEach { extraTag ->
            cmd.addAll(listOf("--tag", extraTag))
        }
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

/**
 * 便捷创建：根据后缀生成镜像标签并注册任务
 */
fun Project.createDockerTask(
    dockerFileRelativePath: String,
    tagSuffix: String,
    taskSuffix: String = tagSuffix,
    imageTagPrefix: String,          // 例如 "ghcr.io/lumklar/sortrss:1.0-"
    buildArgs: Map<String, String> = emptyMap(),
    tagAsLatestVariant: Boolean = true,
    tagAsGlobalLatest: Boolean = false,
    vararg dependencies: String
) {
    val taskName = "buildDockerImage-$taskSuffix"
    val dockerFileDir = "deploy/docker/$dockerFileRelativePath"
    val imageTag = imageTagPrefix + tagSuffix

    val extraTags = mutableListOf<String>()
    if (tagAsLatestVariant) {
        // 从 imageTagPrefix 提取 registry/namespace/image 部分，用于 latest-<suffix>
        val baseWithoutVersion = imageTagPrefix.substringBeforeLast(":") // 去掉 ":version-"
        extraTags.add("$baseWithoutVersion:latest-$tagSuffix")
    }
    if (tagAsGlobalLatest) {
        val baseWithoutVersion = imageTagPrefix.substringBeforeLast(":")
        extraTags.add("$baseWithoutVersion:latest")
    }

    createDockerBuildTask(
        taskName = taskName,
        dockerFileDir = dockerFileDir,
        buildArgs = mapOf("VERSION" to (imageTagPrefix.substringAfterLast(":").removeSuffix("-"))) + buildArgs,
        imageTag = imageTag,
        extraTags = extraTags,
        dependencies = dependencies
    )
}

// 重载：当 tagSuffix == taskSuffix 时简化调用
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
 * 创建 "latest-<suffix>" 变体任务（版本标签 + latest-<suffix>）
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
        dependencies = dependencies,
    )
}

/**
 * 同时创建两个任务：一个普通版本（仅版本标签），一个 latest 变体
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
        dependencies = dependencies,
    )
    createLatestDockerTask(
        dockerFileRelativePath = dockerFileRelativePath,
        suffix = suffix,
        imageTagPrefix = imageTagPrefix,
        buildArgs = buildArgs,
        tagAsGlobalLatest = tagAsGlobalLatest,
        dependencies = dependencies,
    )
}