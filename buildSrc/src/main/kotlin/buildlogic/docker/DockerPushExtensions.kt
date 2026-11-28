package buildlogic.docker

import buildlogic.flavors.StringEnum
import buildlogic.utils.gradlewPath
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider

/**
 * 创建多架构 Docker 镜像发布任务（使用 `docker buildx` 构建并推送）。
 * TODO 支持签名
 *
 * 该任务支持多平台构建（如 linux/amd64、linux/arm64），并可将镜像同时推送到多个仓库。
 * 构建参数、环境变量、依赖任务均可配置，并会生成构建元数据 JSON 文件。
 */
internal fun Project.createDockerPublishMultiArchTask(
    taskName: String,
    targetName: String,
    dockerfileDir: String,
    projectVersion: String,
    envImageVersion: Provider<String>,
    envPlatforms: Provider<String>,
    versionLatest: Boolean,
    latest: Boolean,
    targetRepositories: List<String>,
    configuredPlatforms: List<String>,
    envVars: Map<String, String> = emptyMap(),
    dependencies: List<Pair<String, String>> = emptyList(),
    stringEnums: List<StringEnum> = emptyList()
): TaskProvider<DockerPublishMultiArchTask> {
    return tasks.register(taskName, DockerPublishMultiArchTask::class.java) {
        group = "docker-publish-arch"
        description =
            "Build multi-arch Docker image and push to ${targetRepositories.size} repositories × ${stringEnums.size} flavors"

        configureCommon(
            targetName = targetName,
            dockerfileDir = dockerfileDir,
            projectVersion = projectVersion,
            envImageVersion = envImageVersion,
            envPlatforms = envPlatforms,
            versionLatest = versionLatest,
            latest = latest,
            targetRepositories = targetRepositories,
            configuredPlatforms = configuredPlatforms,
            envVars = envVars,
            dependencies = dependencies,
            stringEnums = stringEnums
        )
        metadataFile.set(layout.buildDirectory.file("docker-metadata/${taskName}.json"))
    }
}

/**
 * 创建单平台构建推送任务。任务覆盖 DockerConfig 声明的全部平台，
 * 执行期由 DOCKER_PLATFORMS 决定哪些真正构建。
 *
 * 单平台任务只推带平台后缀的标签（`<tag>-linux-amd64` 等），**不推裸标签**：
 * 裸标签由 [DockerArchMergeTask] 从各架构镜像合成。若这里也推 `latest`，
 * 多个架构会互相覆盖，且任一架构失败时远端会残留单架构的 `latest`。
 */
internal fun Project.createDockerArchBuildTask(
    taskName: String,
    targetName: String,
    dockerfileDir: String,
    projectVersion: String,
    envImageVersion: Provider<String>,
    envPlatforms: Provider<String>,
    versionLatest: Boolean,
    targetRepositories: List<String>,
    configuredPlatforms: List<String>,
    platform: String,
    envVars: Map<String, String> = emptyMap(),
    dependencies: List<Pair<String, String>> = emptyList(),
    stringEnums: List<StringEnum> = emptyList()
): TaskProvider<DockerArchBuildTask> {
    return tasks.register(taskName, DockerArchBuildTask::class.java) {
        group = "docker-publish-arch"
        description = "Build and push Docker image for platform $platform"

        configureCommon(
            targetName = targetName,
            dockerfileDir = dockerfileDir,
            projectVersion = projectVersion,
            envImageVersion = envImageVersion,
            envPlatforms = envPlatforms,
            versionLatest = versionLatest,
            latest = false,
            targetRepositories = targetRepositories,
            configuredPlatforms = configuredPlatforms,
            envVars = envVars,
            dependencies = dependencies,
            stringEnums = stringEnums
        )
        this.platform.set(platform)
        metadataFile.set(layout.buildDirectory.file("docker-metadata/${taskName}.json"))
    }
}

/**
 * 创建把单平台镜像合并成多架构清单的任务。
 */
internal fun Project.createDockerArchMergeTask(
    taskName: String,
    targetName: String,
    dockerfileDir: String,
    projectVersion: String,
    envImageVersion: Provider<String>,
    envPlatforms: Provider<String>,
    versionLatest: Boolean,
    latest: Boolean,
    targetRepositories: List<String>,
    configuredPlatforms: List<String>,
    stringEnums: List<StringEnum> = emptyList()
): TaskProvider<DockerArchMergeTask> {
    return tasks.register(taskName, DockerArchMergeTask::class.java) {
        group = "docker-publish-arch"
        description = "Merge per-arch images into a multi-arch manifest"

        configureCommon(
            targetName = targetName,
            dockerfileDir = dockerfileDir,
            projectVersion = projectVersion,
            envImageVersion = envImageVersion,
            envPlatforms = envPlatforms,
            versionLatest = versionLatest,
            latest = latest,
            targetRepositories = targetRepositories,
            configuredPlatforms = configuredPlatforms,
            envVars = emptyMap(),
            dependencies = emptyList(),
            stringEnums = stringEnums
        )
    }
}

private fun DockerImageTask.configureCommon(
    targetName: String,
    dockerfileDir: String,
    projectVersion: String,
    envImageVersion: Provider<String>,
    envPlatforms: Provider<String>,
    versionLatest: Boolean,
    latest: Boolean,
    targetRepositories: List<String>,
    configuredPlatforms: List<String>,
    envVars: Map<String, String>,
    dependencies: List<Pair<String, String>>,
    stringEnums: List<StringEnum>
) {
    gradlewPath.set(project.gradlewPath())
    rootProjectDir.set(project.rootProject.projectDir)
    this.dockerfileDir.set(project.file(dockerfileDir))
    this.targetName.set(targetName)
    this.projectVersion.set(projectVersion)
    this.versionLatest.set(versionLatest)
    extraLatest.set(latest)
    this.envImageVersion.set(envImageVersion)
    this.envPlatforms.set(envPlatforms)
    this.configuredPlatforms.set(configuredPlatforms)
    this.envVars.set(envVars)
    this.dependencies.set(dependencies)
    this.stringEnums.set(stringEnums)
    this.targetRepositories.set(targetRepositories)
}
