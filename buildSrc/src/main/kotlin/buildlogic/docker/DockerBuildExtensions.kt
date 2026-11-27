package buildlogic.docker

import buildlogic.flavors.StringEnum
import buildlogic.utils.gradlewPath
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider

/**
 * 注册单镜像构建任务。是否带风味参数只影响任务分组与 -P 传参，
 * 执行逻辑统一走 [DockerBuildTask]（含文件锁与子进程依赖构建）。
 */
internal fun Project.createDockerBuildTask(
    taskName: String,
    dockerfileDir: String,
    namespace: String,
    repository: String,
    targetName: String,
    imageVersion: String,
    envVars: Map<String, String> = emptyMap(),
    dependencies: List<Pair<String, String>> = emptyList(),
    stringEnums: List<StringEnum> = emptyList()
): TaskProvider<DockerBuildTask> {
    val dir = project.file(dockerfileDir)
    val (fullImageName, _) = buildDockerCommand(
        projectDir = rootProject.projectDir,
        dockerfileDir = dir,
        namespace = namespace,
        repository = repository,
        targetName = targetName,
        imageVersion = imageVersion,
        envVars = envVars,
        stringEnums = stringEnums
    )

    return tasks.register(taskName, DockerBuildTask::class.java) {
        group = if (stringEnums.isEmpty()) "docker-build" else "docker-build-flavor"
        description = "Build Docker image '$fullImageName' from $dir"

        gradlewPath.set(gradlewPath())
        rootProjectDir.set(rootProject.projectDir)
        this.dockerfileDir.set(dir)
        this.namespace.set(namespace)
        this.repository.set(repository)
        this.targetName.set(targetName)
        this.imageVersion.set(imageVersion)
        this.envVars.set(envVars)
        this.dependencies.set(dependencies)
        this.stringEnums.set(stringEnums)
    }
}
