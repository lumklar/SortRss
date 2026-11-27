package buildlogic.docker

import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import javax.inject.Inject

/**
 * 单一平台的镜像构建并推送。
 *
 * 任务按 DockerConfig.platforms 声明的全集注册；执行时如果 DOCKER_PLATFORMS
 * 只选中其中一部分，其余任务直接跳过，这样任务图不随环境变量变化。
 * 镜像标签带平台后缀，供 [DockerArchMergeTask] 合成多架构清单。
 */
internal abstract class DockerArchBuildTask @Inject constructor(
    private val execOps: ExecOperations
) : DockerImageTask() {

    @get:Input
    abstract val platform: Property<String>

    @get:OutputFile
    abstract val metadataFile: RegularFileProperty

    @TaskAction
    fun build() {
        val targetPlatform = platform.get()
        val platforms = resolvedPlatforms()
        if (targetPlatform !in platforms) {
            logger.lifecycle("Skip $name: platform '$targetPlatform' not selected (${platforms.joinToString()})")
            return
        }

        runDependencies(execOps)

        val suffix = DockerTaskSupport.platformTagSuffix(targetPlatform)
        val fullImageTags = resolvedTags()
            .map { tag -> "$tag-$suffix" }
            .flatMap { repositoryTags(it) }

        val command = DockerTaskSupport.buildxBuildCommand(
            dockerfile = resolveDockerfile(),
            contextDir = rootProjectDir.get(),
            platforms = targetPlatform,
            imageTags = fullImageTags,
            envVars = envVars.getOrElse(emptyMap()),
            imageVersion = projectVersion.get(),
            metadataFile = metadataFile.get().asFile
        )
        runDocker(execOps, fullImageTags, command)
    }
}
