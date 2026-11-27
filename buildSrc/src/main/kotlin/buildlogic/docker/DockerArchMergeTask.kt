package buildlogic.docker

import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import javax.inject.Inject

/**
 * 把 [DockerArchBuildTask] 推出的单平台镜像合并为多架构清单。
 *
 * 使用 `docker buildx imagetools create`，清单源镜像是带平台后缀的标签，
 * 最终标签与原有推送任务保持一致，因此 CI 里既有引用无需改动。
 */
internal abstract class DockerArchMergeTask @Inject constructor(
    private val execOps: ExecOperations
) : DockerImageTask() {

    @TaskAction
    fun merge() {
        val platforms = resolvedPlatforms()
        val configured = configuredPlatforms.getOrElse(emptyList())
        val unknown = platforms.filter { it !in configured }
        if (unknown.isNotEmpty()) {
            throw GradleException(
                "No per-arch task registered for platform(s): ${unknown.joinToString()}. " +
                    "Add them to DockerConfig.platforms."
            )
        }

        val suffixes = platforms.map { DockerTaskSupport.platformTagSuffix(it) }.distinct()
        val repositories = targetRepositories.getOrElse(emptyList())
        if (repositories.isEmpty()) throw GradleException("At least one target repository is required")

        resolvedTags().forEach { tag ->
            repositories.forEach { repository ->
                val sources = suffixes.map { suffix -> "$repository:$tag-$suffix" }
                val command = buildList {
                    add("docker")
                    add("buildx")
                    add("imagetools")
                    add("create")
                    add("--tag")
                    add("$repository:$tag")
                    addAll(sources)
                }
                runDocker(execOps, listOf("$repository:$tag"), command)
                logger.lifecycle("Merged multi-arch image: $repository:$tag from ${sources.joinToString()}")
            }
        }
    }
}
