package buildlogic.docker

import org.gradle.api.GradleException
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import javax.inject.Inject

/**
 * 一次 buildx 构建推送多架构镜像（原有入口，保留兼容）。
 *
 * 分架构构建 + 清单合并由 [DockerArchBuildTask] / [DockerArchMergeTask] 提供。
 */
internal abstract class DockerPublishMultiArchTask @Inject constructor(
    private val execOps: ExecOperations
) : DockerImageTask() {

    @get:OutputFile
    abstract val metadataFile: RegularFileProperty

    @TaskAction
    fun publish() {
        runDependencies(execOps)

        val dockerfile = resolveDockerfile()
        val repos = targetRepositories.getOrElse(emptyList())
        val tags = resolvedTags()
        val platforms = resolvedPlatforms()
        if (repos.isEmpty() || tags.isEmpty() || platforms.isEmpty()) {
            throw GradleException("At least one repository, one tag and one platform are required")
        }

        val platformList = platforms.joinToString(",")
        val fullImageTags = repos.flatMap { repo -> tags.map { tag -> "$repo:$tag" } }
        val command = DockerTaskSupport.buildxBuildCommand(
            dockerfile = dockerfile,
            contextDir = rootProjectDir.get(),
            platforms = platformList,
            imageTags = fullImageTags,
            envVars = envVars.getOrElse(emptyMap()),
            imageVersion = projectVersion.get(),
            metadataFile = metadataFile.get().asFile
        )

        runDocker(execOps, fullImageTags, command)

        val metaFile = metadataFile.get().asFile
        if (metaFile.exists()) {
            logger.lifecycle("Docker build metadata written to ${metaFile.absolutePath}")
        }
    }
}
