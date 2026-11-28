package buildlogic.docker

import buildlogic.constant.EnvConstant
import buildlogic.flavors.StringEnum
import buildlogic.utils.NameUtils
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider

/**
 * 装配 Docker 任务。
 *
 * - build / flavor build：`docker build` 单镜像
 * - push：`docker buildx` 一次构建推送多架构镜像（兼容原有 CI 入口）
 * - pushArch + mergeArch：逐平台推送单架构镜像，再用 imagetools 合成清单
 *
 * 环境变量（DOCKER_IMAGE_VERSION / DOCKER_PLATFORMS）以 Provider 形式传入任务，
 * 在执行期解析；任务图只取决于 DockerConfig，不因环境变量变化。
 */
fun Project.createDockerTask(
    configs: List<DockerConfig> = emptyList()
) {
    val buildProviders = mutableListOf<TaskProvider<*>>()
    val flavorBuildProviders = mutableListOf<TaskProvider<*>>()
    val pushProviders = mutableListOf<TaskProvider<*>>()
    val archBuildProviders = mutableListOf<TaskProvider<*>>()
    val mergeProviders = mutableListOf<TaskProvider<*>>()

    val projectVersion = project.version.toString()
    NameUtils.validateVersion(projectVersion)
    val version = projectVersion.lowercase()
    val envImageVersion = providers.environmentVariable(EnvConstant.DOCKER_IMAGE_VERSION).orElse("")
    val envPlatforms = providers.environmentVariable(EnvConstant.DOCKER_PLATFORMS).orElse("")

    for (config in configs) {
        val buildProvider = createDockerBuildTask(
            taskName = getTaskName("buildDockerImage", config.targetName),
            dockerfileDir = config.dockerfileDir,
            namespace = config.namespace,
            repository = config.repository,
            targetName = config.targetName,
            imageVersion = version,
            envVars = config.envVars,
            dependencies = config.dependencies
        )
        buildProviders.add(buildProvider)

        val targetRepositories = config.registryList.map { "$it/${config.namespace}/${config.repository}" }

        for (enums in config.stringEnums) {
            val flavorBuildProvider = createDockerBuildTask(
                taskName = getTaskName("buildFlavorDockerImage", config.targetName, enums),
                dockerfileDir = config.dockerfileDir,
                namespace = config.namespace,
                repository = config.repository,
                targetName = config.targetName,
                imageVersion = version,
                envVars = config.envVars,
                dependencies = config.dependencies,
                stringEnums = enums
            )
            flavorBuildProviders.add(flavorBuildProvider)

            pushProviders.add(
                createDockerPublishMultiArchTask(
                    taskName = getTaskName("pushMultiArchFlavorDockerImage", config.targetName, enums),
                    targetName = config.targetName,
                    dockerfileDir = config.dockerfileDir,
                    projectVersion = version,
                    envImageVersion = envImageVersion,
                    envPlatforms = envPlatforms,
                    versionLatest = config.versionLatest,
                    latest = config.latest,
                    targetRepositories = targetRepositories,
                    configuredPlatforms = config.platforms,
                    envVars = config.envVars,
                    dependencies = config.dependencies,
                    stringEnums = enums
                )
            )

            val archProviders = config.platforms.map { platform ->
                createDockerArchBuildTask(
                    taskName = getTaskName("pushArchDockerImage", config.targetName, enums, platform),
                    targetName = config.targetName,
                    dockerfileDir = config.dockerfileDir,
                    projectVersion = version,
                    envImageVersion = envImageVersion,
                    envPlatforms = envPlatforms,
                    versionLatest = config.versionLatest,
                    targetRepositories = targetRepositories,
                    configuredPlatforms = config.platforms,
                    platform = platform,
                    envVars = config.envVars,
                    dependencies = config.dependencies,
                    stringEnums = enums
                )
            }
            archBuildProviders.addAll(archProviders)

            val mergeProvider = createDockerArchMergeTask(
                taskName = getTaskName("mergeArchDockerImage", config.targetName, enums),
                targetName = config.targetName,
                dockerfileDir = config.dockerfileDir,
                projectVersion = version,
                envImageVersion = envImageVersion,
                envPlatforms = envPlatforms,
                versionLatest = config.versionLatest,
                latest = config.latest,
                targetRepositories = targetRepositories,
                configuredPlatforms = config.platforms,
                stringEnums = enums
            )
            archProviders.forEach { provider -> mergeProvider.configure { dependsOn(provider) } }
            mergeProviders.add(mergeProvider)
        }
    }

    fun registerAggregateTask(name: String, providers: Collection<TaskProvider<*>>, groupName: String, desc: String) {
        val existing = project.tasks.findByName(name)
        if (existing == null) {
            project.tasks.register(name) {
                group = groupName
                description = desc
                dependsOn(providers)
            }
        } else {
            existing.dependsOn(providers)
        }
    }

    registerAggregateTask(
        name = "buildAllDockerImages",
        providers = buildProviders,
        groupName = "docker-build",
        desc = "Build All Docker image"
    )
    registerAggregateTask(
        name = "buildAllFlavorDockerImages",
        providers = flavorBuildProviders,
        groupName = "docker-build-flavor",
        desc = "Build All Flavor Docker image"
    )
    registerAggregateTask(
        name = "pushAllMultiArchFlavorDockerImage",
        providers = pushProviders,
        groupName = "docker-publish-arch",
        desc = "Build All multi-arch Docker image and push"
    )
    registerAggregateTask(
        name = "pushAllArchFlavorDockerImage",
        providers = archBuildProviders,
        groupName = "docker-publish-arch",
        desc = "Build and push all per-arch Docker images"
    )
    registerAggregateTask(
        name = "mergeAllArchFlavorDockerImage",
        providers = mergeProviders,
        groupName = "docker-publish-arch",
        desc = "Merge all per-arch images into multi-arch manifests"
    )
}

fun getTaskName(
    namePrefix: String,
    targetName: String,
    stringEnums: List<StringEnum> = emptyList(),
    platform: String? = null
): String {
    var name = namePrefix + "-" + targetName + stringEnums.joinToString("") { "-" + it.value }
    if (!platform.isNullOrBlank()) {
        name += "-" + DockerTaskSupport.platformTagSuffix(platform)
    }
    return NameUtils.toLowerCamelCase(name)
}
