package buildlogic.docker

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.Exec

/**
 * 创建 Docker 镜像发布任务（类似 createDockerBuildTask 风格）
 * @param taskName 发布任务名称，如 "publishDocker"
 * @param buildTaskName 已有构建任务的名称，如 "buildDocker"
 * @param buildModule 构建任务所在模块路径，如 ":" 或 ":app"
 * @param sourceImage 原始镜像名称，如 "ghcr.io/lumklar:sortrss-latest"
 * @param registryAndNamespaces 命名空间列表，带 registry，如 ["docker.io/myuser", "reg.example.com/team"]
 * @param repositoryAndTags 仓库和标签列表，如 ["sortrss/latest"]
 */
fun Project.createDockerPublishTask(
    taskName: String,
    buildTaskName: String,
    buildModule: String,
    sourceImage: String,
    registryAndNamespaces: List<String>,
    repositoryAndTags: List<String>
) {
    // 获取目标模块的项目，用于依赖构建任务
    val targetProject = if (buildModule == ":") rootProject else project(buildModule)
    val buildTask = targetProject.tasks.named(buildTaskName)

    tasks.register(taskName, Exec::class.java) {
        group = "docker-publish"
        description =
            "Tag and push $sourceImage to ${registryAndNamespaces.size} namespaces × ${repositoryAndTags.size} tags"

        // 添加对构建任务的依赖
        dependsOn(buildTask)

        // 笛卡尔积目标
        val targets = registryAndNamespaces.flatMap { ns ->
            repositoryAndTags.map { tag -> "$ns/$tag" }
        }

        targets.forEach { target ->
            logger.lifecycle("Tagging and pushing: $target")

            //TODO多行命令是否生效？
            // 使用 exec 执行 docker 命令
            commandLine("docker", "tag", sourceImage, target)
            commandLine("docker", "push", target)
        }
    }
}
