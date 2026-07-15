package buildlogic.docker

import buildlogic.flavors.StringEnum
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import java.io.File
import java.time.Instant
import javax.inject.Inject

internal abstract class DockerPublishMultiArchTask @Inject constructor(
    private val execOps: ExecOperations
) : DefaultTask() {

    @get:Input
    abstract val gradlewPath: Property<String>

    @get:Input
    abstract val rootProjectDir: Property<File>

    @get:Input
    abstract val dockerfileDir: Property<File>

    // 以下参数主要用于构建标签或元数据，实际推送的目标由 targetRepositories + tags 决定
    @get:Input
    abstract val imageVersion: Property<String>

    @get:Input
    abstract val envVars: MapProperty<String, String>

    @get:Input
    abstract val dependencies: ListProperty<Pair<String, String>>

    @get:Input
    abstract val stringEnums: ListProperty<StringEnum>

    @get:Input
    abstract val targetRepositories: ListProperty<String>   // 例如 ["docker.io/myuser/app", "ghcr.io/myorg/app"]

    @get:Input
    abstract val tags: ListProperty<String>                 // 例如 ["latest", "v1.0"]

    @get:Input
    abstract val targetPlatforms: ListProperty<String>            // 默认 ["linux/amd64", "linux/arm64"]

    @get:OutputFile
    abstract val metadataFile: RegularFileProperty          // 构建元数据输出路径

    @TaskAction
    fun publish() {
        // 1. 执行依赖任务（子进程）
        val deps = dependencies.getOrElse(emptyList())
        if (deps.isNotEmpty()) {
            val taskPaths = deps.map { (module, task) -> "$module:$task" }
            val command = mutableListOf<String>().apply {
                add(gradlewPath.get())
                addAll(taskPaths)
                stringEnums.getOrElse(emptyList()).forEach { enum ->
                    add("-P${enum.envKey}=${enum.value}")
                }
            }
            execOps.exec {
                workingDir = rootProjectDir.get()
                commandLine = command
            }
        }

        // 2. 多平台构建并推送
        val dockerfile = dockerfileDir.get().resolve("Dockerfile")
        if (!dockerfile.exists()) {
            throw GradleException("Dockerfile not found: ${dockerfile.absolutePath}")
        }

        val repos = targetRepositories.getOrElse(emptyList())
        val tagList = tags.getOrElse(emptyList())
        val platforms = targetPlatforms.getOrElse(emptyList())
        if (repos.isEmpty() || tagList.isEmpty() || platforms.isEmpty()) {
            throw GradleException("At least one repository and one tag and one platform are required")
        }

        val fullImageTags = repos.flatMap { repo ->
            tagList.map { tag -> "$repo:$tag" }
        }

        val platformList = platforms.joinToString(",")

        val command = mutableListOf<String>().apply {
            add("docker")
            add("buildx")
            add("build")
            add("--platform")
            add(platformList)
            add("--push")
            fullImageTags.forEach { fullTag ->
                add("--tag")
                add(fullTag)
            }
            // 构建参数
            envVars.getOrElse(emptyMap()).forEach { (key, value) ->
                add("--build-arg")
                add("$key=$value")
            }
            // OCI 标签（元数据）
            if (imageVersion.isPresent) {
                add("--label")
                add("org.opencontainers.image.version=${imageVersion.get()}")
            }
            add("--label")
            add("org.opencontainers.image.created=${Instant.now().toString()}")
            // 输出元数据文件
            add("--metadata-file")
            add(metadataFile.get().asFile.absolutePath)
            // Dockerfile 路径
            add("-f")
            add(dockerfile.absolutePath)              // Dockerfile 用绝对路径
            add(rootProjectDir.get().absolutePath)    // 上下文改为项目根目录
        }

        execOps.exec {
            workingDir = rootProjectDir.get()
            commandLine = command
        }

        // 打印元数据位置
        val metaFile = metadataFile.get().asFile
        if (metaFile.exists()) {
            logger.lifecycle("Docker build metadata written to ${metaFile.absolutePath}")
        }
    }
}
