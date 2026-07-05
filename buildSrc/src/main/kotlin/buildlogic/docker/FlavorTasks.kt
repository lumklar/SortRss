package buildlogic.docker

import buildlogic.constant.EnvConstant
import buildlogic.constant.PropertiesContant
import buildlogic.utils.EnvPropertyConverter
import buildlogic.utils.getConfigString
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.register
import org.gradle.process.ExecOperations
import javax.inject.Inject

/**
 * 推送 Docker 镜像任务
 * 单平台：先基于 sourceTag 打上所有 tag，再依次推送
 * 多平台：构建时已推送，此任务直接跳过
 */
abstract class DockerPushTask @Inject constructor(
    private val execOps: ExecOperations
) : DefaultTask() {
    @get:Input
    abstract val tags: ListProperty<String>

    @get:Input
    abstract val multiPlatformEnabled: Property<Boolean>

    @get:Input
    abstract val sourceTag: Property<String>   // 单平台下本地构建的主标签

    @TaskAction
    fun push() {
        if (multiPlatformEnabled.get()) {
            println("Multi‑platform mode enabled, image already pushed during build. Skipping separate push.")
            return
        }

        val source = sourceTag.get()
        val tagsToPush = tags.get()
        // 为所有推送标签打 tag（如果与源不同）
        tagsToPush.filter { it != source }.forEach { tag ->
            execOps.exec {
                commandLine("docker", "tag", source, tag)
            }
        }
        // 推送所有标签
        tagsToPush.forEach { tag ->
            execOps.exec {
                commandLine("docker", "push", tag)
            }
        }
    }
}

/**
 * 为每个 DockerTaskConfig 创建风味包装任务和推送任务
 * @return Pair<wrapper任务名列表, push任务名列表>
 */
fun Project.createFlavorWrapperTasks(
    configs: List<DockerTaskConfig>,
    imageNamePrefix: String,   // 无 Registry 前缀，如 "lumklar/sortrss:1.0-"
    dockerRegistry: String,    // 原始配置，可包含逗号
    dockerNamespace: String,
    dockerImageName: String,
    isLatest: Boolean = true   // 新增参数，控制是否为 Latest 版本
): Pair<List<String>, List<String>> {
    val wrapperTaskNames = mutableListOf<String>()
    val pushTaskNames = mutableListOf<String>()

    // 解析多个 Registry
    val registries = dockerRegistry.split(",").map { it.trim() }.filter { it.isNotEmpty() }

    // 从 imageNamePrefix 提取 version
    val version = parseImageNamePrefix(imageNamePrefix).third
    // 根据 isLatest 决定任务名中的后缀
    val taskSuffix = if (isLatest) "-latest" else ""

    configs.forEach { config ->
        val flavors = config.flavors
        for (flavor in flavors) {
            val flavorSuffix = buildFlavorSuffix(flavor.getFlavors())
            // 任务名使用动态后缀
            val wrapperTaskName = "buildDockerImage-${config.suffix}${flavorSuffix}$taskSuffix"
            val baseTaskName = "buildDockerImage-${config.suffix}-latest"  // 基础任务名保持不变
            val imageTagSuffix = "${config.suffix}${flavorSuffix}"

            // 注册 wrapper 任务
            tasks.register(wrapperTaskName, Exec::class.java) {
                group = "docker"
                description = "Build Docker image with flavor ${flavor.toKeyValueString()} using ${config.suffix}"
                for (f in flavor.getFlavors()) {
                    environment(f.envKey, f.value)
                }
                environment(
                    EnvPropertyConverter.propertyToEnv(PropertiesContant.DOCKER_TAG_SUFFIX),
                    imageTagSuffix
                )

                val gradlew = if (System.getProperty("os.name").lowercase().contains("windows")) {
                    "./gradlew.bat"
                } else {
                    "./gradlew"
                }
                commandLine(gradlew, baseTaskName, "--stacktrace")
                workingDir = project.rootDir
            }
            wrapperTaskNames.add(wrapperTaskName)

            // 计算标签，将 tagAsLatestVariant 设为 isLatest
            val imageTags = resolveImageTags(
                namespace = dockerNamespace,
                imageName = dockerImageName,
                version = version,
                suffix = config.suffix,
                flavors = flavor.getFlavors(),
                tagAsLatestVariant = isLatest,      // 根据参数控制是否生成 latest 标签
                tagAsGlobalLatest = config.tagAsGlobalLatest,
                registries = registries
            )

            val pushTaskName = "pushDockerImage-${config.suffix}${flavorSuffix}$taskSuffix"
            tasks.register<DockerPushTask>(pushTaskName) {
                group = "docker"
                description = "Push Docker image for flavor ${flavor.toKeyValueString()} using ${config.suffix}"
                dependsOn(wrapperTaskName)

                this.tags.set(imageTags.pushTags)
                this.sourceTag.set(imageTags.primaryBuildTag)
                // 判断多平台
                val platforms = project.getConfigString(EnvConstant.DOCKER_PLATFORMS)
                this.multiPlatformEnabled.set(!platforms.isNullOrBlank())
            }
            pushTaskNames.add(pushTaskName)
        }
    }

    return Pair(wrapperTaskNames, pushTaskNames)
}