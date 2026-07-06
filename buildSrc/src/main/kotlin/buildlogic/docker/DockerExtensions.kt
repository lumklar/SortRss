import buildlogic.docker.buildDockerCommand
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.Exec
import buildlogic.flavors.StringEnum
import buildlogic.release.DockerBuildTask
import java.io.File

/**
 * 原方法：无 stringEnums，使用 Exec 任务直接执行 docker build，依赖通过 dependsOn 添加（零额外开销）
 */
fun Project.createDockerBuildTask(
    taskName: String,
    dockerfileDir: Any,
    namespace: String,
    projectName: String,
    imageName: String,
    imageVersion: String,
    envVars: Map<String, String> = emptyMap(),
    dependencies: List<Pair<String, String>> = emptyList()
): Task {
    val dir = project.file(dockerfileDir)
    val (fullImageName, dockerCommand) = buildDockerCommand(dir, namespace, projectName+"-"+imageName, imageVersion, envVars)

    return tasks.register(taskName, Exec::class.java) {
        group = "docker-build"
        description = "Build Docker image '$fullImageName' from $dir"
        commandLine = dockerCommand
    }.get().also { task ->
        dependencies.forEach { (modulePath, taskNameDep) ->
            task.dependsOn("$modulePath:$taskNameDep")
        }
    }
}

/**
 * 新方法：带 stringEnums，使用自定义 DockerBuildTask，依赖任务在子进程中执行以隔离参数
 */
fun Project.createDockerBuildTask(
    taskName: String,
    dockerfileDir: Any,
    namespace: String,
    projectName: String,
    imageName: String,
    imageVersion: String,
    envVars: Map<String, String> = emptyMap(),
    dependencies: List<Pair<String, String>> = emptyList(),
    stringEnums: List<StringEnum>
): Task {
    val gradlew = if (File(rootProject.projectDir, "gradlew.bat").exists()) "gradlew.bat" else "gradlew"
    return tasks.register(taskName, DockerBuildTask::class.java) {
        val dir = project.file(dockerfileDir)
        group = "docker-build-flavor"
        description = "Build Docker flavor image from $dir"

        gradlewPath.set(rootProject.projectDir.resolve(gradlew).absolutePath)
        rootProjectDir.set(rootProject.projectDir)
        this.dockerfileDir.set(project.file(dockerfileDir))
        this.namespace.set(namespace)
        this.imageName.set(projectName+"-"+imageName)
        this.imageVersion.set(imageVersion)
        this.envVars.set(envVars)
        this.dependencies.set(dependencies)
        this.stringEnums.set(stringEnums)
    }.get()
}