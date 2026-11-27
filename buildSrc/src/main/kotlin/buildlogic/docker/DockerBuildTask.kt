package buildlogic.docker

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
        val rootDir = rootProjectDir.get()
        DockerTaskSupport.runDependencyTasks(
            execOps = execOps,
            gradlewPath = gradlewPath.get(),
            rootDir = rootDir,
            dependencies = dependencies.getOrElse(emptyList()),
            stringEnums = stringEnums.getOrElse(emptyList())
        )

        val (fullImageName, dockerCommand) = buildDockerCommand(
            projectDir = rootDir,
            dockerfileDir = dockerfileDir.get(),
            namespace = namespace.get(),
            repository = repository.get(),
            targetName = targetName.get(),
            imageVersion = imageVersion.get(),
            envVars = envVars.get(),
            stringEnums = stringEnums.get()
        )
        DockerTaskSupport.runDockerCommand(execOps, rootDir, listOf(fullImageName), dockerCommand)
    }
}
