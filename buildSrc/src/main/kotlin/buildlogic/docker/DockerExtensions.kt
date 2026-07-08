package buildlogic.docker

import buildlogic.constant.EnvConstant
import buildlogic.flavors.StringEnum
import buildlogic.utils.NameUtils
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider

fun Project.createDockerTask(
    configs: List<DockerConfig> = emptyList()
) {
    //TODO 支持额外的标签开头：版本号-hash
    //TODO 把环境变量改为运行时获取
    //TODO 如果同名配置很多，说明不同架构dockerfile不一致，需要处理
    //TODO 多任务，子模块加文件锁防冲突，使用公共的方法
    //TODO 增加分架构实例部署任务和合并任务
    val buildProviders = mutableListOf<TaskProvider<*>>()
    val flavorBuildProviders = mutableListOf<TaskProvider<*>>()
    val pushProviders = mutableListOf<TaskProvider<*>>()

    val projectVersion = project.version.toString()
    NameUtils.validateVersion(projectVersion)
    val version = projectVersion.lowercase()
    val envVersion = System.getenv(EnvConstant.DOCKER_IMAGE_VERSION)
    val envPlatforms = System.getenv(EnvConstant.DOCKER_PLATFORMS)

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

            //根据环境变量或配置决定 platforms
            val platforms = if (!envPlatforms.isNullOrBlank()) {
                envPlatforms.split(',').map { it.trim() }
            } else {
                config.platforms
            }

            // 生成 tags
            val tags = ArrayList<String>()
            val versions = ArrayList<String>()
            if (!envVersion.isNullOrBlank()) {
                versions.addAll(
                    envVersion.split(',')
                        .map {
                            val trim = it.trim()
                            NameUtils.toKebabCase(it)
                        }
                        .filter { it.isNotBlank() }
                )
            } else {
                versions.add(version);
                if (config.versionLatest) {
                    versions.add("latest")
                }
                // 如果 config 有 latest 标志，额外添加 "latest" 标签
                if (config.latest) {
                    tags.add("latest")
                }
            }
            versions.forEach { ver ->
                tags.add(buildDockerTag(ver, config.targetName, enums))
            }

            val pushProvider = createDockerPublishMultiArchTask(
                taskName = getTaskName("pushMultiArchFlavorDockerImage", config.targetName, enums),
                dockerfileDir = config.dockerfileDir,
                imageVersion = version,
                envVars = config.envVars,
                dependencies = config.dependencies,
                stringEnums = enums,
                targetRepositories = config.registryList.map { "$it/${config.namespace}/${config.repository}" },
                tags = tags,
                platforms = platforms
            )
            pushProviders.add(pushProvider)
        }
    }

    // 注册三个聚合任务（如果已存在则追加依赖）
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
}

fun getTaskName(
    namePrefix: String,
    targetName: String,
    stringEnums: List<StringEnum> = emptyList()
): String {
    var name = namePrefix + "-" + targetName + stringEnums.joinToString("") { "-" + it.value }
    return NameUtils.toLowerCamelCase(name)
}
