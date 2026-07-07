package buildlogic.docker

import buildlogic.flavors.StringEnum
import org.gradle.api.Project
import java.io.File

/**
 * 创建多架构 Docker 镜像发布任务（使用 `docker buildx` 构建并推送）。
 * TODO 支持签名
 *
 * 该任务支持多平台构建（如 linux/amd64、linux/arm64），并可将镜像同时推送到多个仓库。
 * 构建参数、环境变量、依赖任务均可配置，并会生成构建元数据 JSON 文件。
 *
 * @param taskName 任务名称（如 "publishApp"）
 * @param dockerfileDir Dockerfile 所在目录路径（相对于项目根目录）
 * @param imageVersion 镜像版本号（可选，用于 OCI 标签 `org.opencontainers.image.version`）
 * @param envVars 构建时传入的环境变量（`--build-arg`）
 * @param dependencies 前置依赖任务列表，格式为 `(模块路径, 任务名)`，执行时通过子 Gradle 进程调用
 * @param stringEnums 枚举值列表，会以 `-Pkey=value` 形式传递给依赖任务的子进程
 * @param targetRepositories 目标仓库完整前缀列表，如 `["docker.io/myuser/app", "ghcr.io/myorg/app"]`
 * @param tags 镜像标签列表，如 `["latest", "v1.0"]`，会与每个仓库拼接为完整标签
 * @param platforms 目标平台列表，如 `["linux/amd64", "linux/arm64"]`，默认可由实现决定
 */
internal fun Project.createDockerPublishMultiArchTask(
    taskName: String,
    dockerfileDir: String,
    imageVersion: String?,
    envVars: Map<String, String> = emptyMap(),
    dependencies: List<Pair<String, String>> = emptyList(),
    stringEnums: List<StringEnum> = emptyList(),
    targetRepositories: List<String>,   // 完整的仓库前缀，如 "docker.io/myuser/app"
    tags: List<String>,
    platforms: List<String>
) {
    val gradlew = if (File(rootProject.projectDir, "gradlew.bat").exists()) "gradlew.bat" else "gradlew"

    tasks.register(taskName, DockerPublishMultiArchTask::class.java) {
        group = "docker-publish"
        description =
            "Build multi-arch Docker image and push to ${targetRepositories.size} repositories × ${tags.size} tags"

        gradlewPath.set(rootProject.projectDir.resolve(gradlew).absolutePath)
        rootProjectDir.set(rootProject.projectDir)
        this.dockerfileDir.set(project.file(dockerfileDir))
        this.imageVersion.set(imageVersion)
        this.envVars.set(envVars)
        this.dependencies.set(dependencies)
        this.stringEnums.set(stringEnums)
        this.targetRepositories.set(targetRepositories)
        this.tags.set(tags)
        this.targetPlatforms.set(platforms)

        // 元数据文件路径：build/docker-metadata/<taskName>.json
        this.metadataFile.set(
            project.layout.buildDirectory.file("docker-metadata/${taskName}.json")
        )
    }
}
