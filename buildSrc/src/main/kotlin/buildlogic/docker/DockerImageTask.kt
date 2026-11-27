package buildlogic.docker

import buildlogic.flavors.StringEnum
import org.gradle.api.DefaultTask
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.process.ExecOperations
import java.io.File

/**
 * Docker 镜像类任务的公共输入与执行期解析逻辑。
 *
 * 环境变量通过 Provider 注入，任务执行时才解析标签与平台列表；
 * 这样配置缓存命中后改动 DOCKER_IMAGE_VERSION / DOCKER_PLATFORMS 仍然生效。
 */
internal abstract class DockerImageTask : DefaultTask() {

    @get:Input
    abstract val gradlewPath: Property<String>

    @get:Input
    abstract val rootProjectDir: Property<File>

    @get:Input
    abstract val dockerfileDir: Property<File>

    @get:Input
    abstract val targetName: Property<String>

    @get:Input
    abstract val projectVersion: Property<String>

    @get:Input
    abstract val versionLatest: Property<Boolean>

    @get:Input
    abstract val extraLatest: Property<Boolean>

    @get:Input
    abstract val envImageVersion: Property<String>

    @get:Input
    abstract val envPlatforms: Property<String>

    @get:Input
    abstract val configuredPlatforms: ListProperty<String>

    @get:Input
    abstract val envVars: MapProperty<String, String>

    @get:Input
    abstract val dependencies: ListProperty<Pair<String, String>>

    @get:Input
    abstract val stringEnums: ListProperty<StringEnum>

    @get:Input
    abstract val targetRepositories: ListProperty<String>

    protected fun resolvedTags(): List<String> = DockerTaskSupport.resolveTags(
        envVersion = envImageVersion.getOrElse(""),
        projectVersion = projectVersion.get(),
        versionLatest = versionLatest.getOrElse(false),
        extraLatest = extraLatest.getOrElse(false),
        targetName = targetName.get(),
        stringEnums = stringEnums.getOrElse(emptyList())
    )

    protected fun resolvedPlatforms(): List<String> = DockerTaskSupport.resolvePlatforms(
        envPlatforms = envPlatforms.getOrElse(""),
        configured = configuredPlatforms.getOrElse(emptyList())
    )

    protected fun runDependencies(execOps: ExecOperations) {
        DockerTaskSupport.runDependencyTasks(
            execOps = execOps,
            gradlewPath = gradlewPath.get(),
            rootDir = rootProjectDir.get(),
            dependencies = dependencies.getOrElse(emptyList()),
            stringEnums = stringEnums.getOrElse(emptyList())
        )
    }

    protected fun resolveDockerfile(): File = DockerTaskSupport.dockerfileIn(dockerfileDir.get())

    protected fun runDocker(execOps: ExecOperations, imageTags: List<String>, command: List<String>) {
        DockerTaskSupport.runDockerCommand(execOps, rootProjectDir.get(), imageTags, command)
    }

    protected fun repositoryTags(tag: String): List<String> =
        targetRepositories.getOrElse(emptyList()).map { repository -> "$repository:$tag" }
}
