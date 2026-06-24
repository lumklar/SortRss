package buildlogic.docker

import buildlogic.utils.getConfigString
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.register
import org.gradle.process.ExecOperations
import javax.inject.Inject

abstract class DockerPushTask @Inject constructor(
    private val execOps: ExecOperations
) : DefaultTask() {
    @get:Input
    abstract val tags: ListProperty<String>

    @TaskAction
    fun push() {
        //TODO 统一管理key，统一管理isMultiPlatform的逻辑
        val isMultiPlatform = project.getConfigString("DOCKER_PLATFORMS")?.isNotEmpty() ?: false
        if (isMultiPlatform) {
            println("Multi‑platform mode enabled, image already pushed during build. Skipping separate push.")
            return
        }
        tags.get().forEach { tag ->
            execOps.exec {  // 使用注入的 ExecOperations
                commandLine("docker", "push", tag)
            }
        }
    }
}

/**
 * 为每个配置创建风味包装任务（wrapper + push）
 * @param configs DockerTaskConfig 列表
 * @param imageNamePrefix 镜像标签前缀（包含版本）
 * @param dockerRegistry 镜像仓库
 * @param dockerNamespace 命名空间
 * @param dockerImageName 镜像名
 * @return Pair<wrapper任务名列表, push任务名列表>
 */
fun Project.createFlavorWrapperTasks(
    configs: List<DockerTaskConfig>,
    imageNamePrefix: String,
    dockerRegistry: String,
    dockerNamespace: String,
    dockerImageName: String
): Pair<List<String>, List<String>> {
    val wrapperTaskNames = mutableListOf<String>()
    val pushTaskNames = mutableListOf<String>()

    configs.forEach { config ->
        val flavor = config.flavors
        if (flavor != null) {
            //TODO 自动获取所有的
            val flavorSuffix = "-${flavor.data.value.lowercase()}"
            val wrapperTaskName = "buildDockerImage-${config.suffix}${flavorSuffix}-latest"
            val baseTaskName = "buildDockerImage-${config.suffix}-latest"

            // 注册 wrapper 任务（不变）
            tasks.register(wrapperTaskName, Exec::class.java) {
                group = "docker"
                description = "Build Docker image with flavor data=${flavor.data} using ${config.suffix}"
                //TODO 自动转换，非写死
                environment("FLAVOR_DATA", flavor.data.toString())

                val gradlew = if (System.getProperty("os.name").lowercase().contains("windows")) {
                    "./gradlew.bat"
                } else {
                    "./gradlew"
                }
                commandLine(gradlew, baseTaskName, "--stacktrace")
                workingDir = project.rootDir
            }
            wrapperTaskNames.add(wrapperTaskName)

            // 注册 push 任务 —— 使用自定义任务
            val pushTaskName = "pushDockerImage-${config.suffix}${flavorSuffix}-latest"
            tasks.register<DockerPushTask>(pushTaskName) {
                group = "docker"
                description = "Push Docker image for flavor ${flavor.data} using ${config.suffix}"
                dependsOn(wrapperTaskName)

                // 计算所有标签并设置到 tags 属性
                val tags = mutableListOf<String>()
                tags.add("$imageNamePrefix${config.suffix}")
                tags.add("$dockerRegistry/$dockerNamespace/$dockerImageName:latest-${config.suffix}")
                if (config.tagAsGlobalLatest) {
                    tags.add("$dockerRegistry/$dockerNamespace/$dockerImageName:latest")
                }
                this.tags.set(tags)
            }
            pushTaskNames.add(pushTaskName)
        }
    }

    return Pair(wrapperTaskNames, pushTaskNames)
}