package buildlogic.release

import buildlogic.docker.buildDockerCommand
import buildlogic.flavors.StringEnum
import org.gradle.api.DefaultTask
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import java.io.File
import javax.inject.Inject

internal abstract class DockerBuildTask @Inject constructor(
    private val execOps: ExecOperations
) : DefaultTask() {

    @get:Input
    abstract val gradlewPath: Property<String>

    @get:Input
    abstract val rootProjectDir: Property<File>

    @get:Input
    abstract val dockerfileDir: Property<File>

    @get:Input
    abstract val namespace: Property<String>

    @get:Input
    abstract val repository: Property<String>

    @get:Input
    abstract val targetName: Property<String>

    @get:Input
    abstract val imageVersion: Property<String>

    @get:Input
    abstract val envVars: MapProperty<String, String>

    @get:Input
    abstract val dependencies: ListProperty<Pair<String, String>>

    @get:Input
    abstract val stringEnums: ListProperty<StringEnum>

    @TaskAction
    fun build() {
        // 1. 执行依赖任务（子进程隔离）
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

        // 2. 执行 docker build（使用公共函数）
        val (_, dockerCommand) = buildDockerCommand(
            dockerfileDir = dockerfileDir.get(),
            namespace = namespace.get(),
            repository = repository.get(),
            targetName = targetName.get(),
            imageVersion = imageVersion.get(),
            envVars = envVars.getOrElse(emptyMap())
        )
        execOps.exec {
            workingDir = rootProjectDir.get()
            commandLine = dockerCommand
        }
    }
}