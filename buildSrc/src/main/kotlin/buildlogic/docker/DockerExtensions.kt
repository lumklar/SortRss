package buildlogic.docker

import buildlogic.constant.EnvConstant
import buildlogic.flavors.StringEnum
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider
import org.gradle.internal.impldep.org.bouncycastle.asn1.x509.Target.targetName

fun Project.createDockerTask(
    configs: List<DockerConfig> = emptyList()
) {
    //TODO 把环境变量改为运行时获取
    //TODO 如果同名配置很多，说明不同架构dockerfile不一致，需要处理
    //TODO 多任务，子模块加文件锁防冲突，使用公共的方法
    //TODO 增加分架构实例部署任务和合并任务
    val buildProviders = mutableListOf<TaskProvider<*>>()
    val flavorBuildProviders = mutableListOf<TaskProvider<*>>()
    val pushProviders = mutableListOf<TaskProvider<*>>()

    val version = project.version.toString()
    val envVersion = System.getenv(EnvConstant.DOCKER_IMAGE_VERSION)
    val envPlatforms = System.getenv(EnvConstant.DOCKER_PLATFORMS)

    for (config in configs) {
        val buildTaskName = getTaskName("buildDockerImage", config.targetName)
        val buildProvider = createDockerBuildTask(
            taskName = buildTaskName,
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
            val flavorBuildTaskName = getTaskName("buildFlavorDockerImage", config.targetName, enums)

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
            var versions = ArrayList<String>()
            if (!envVersion.isNullOrBlank()) {
                versions.add(envVersion)
            } else {
                versions.add(version);
                if (config.versionLatest) {
                    versions.add("latest")
                }
            }
            var tags = ArrayList<String>()
            versions.forEach { ver ->
                tags.add(buildDockerTag(ver, config.targetName, enums))
            }
            // 如果 config 有 latest 标志，额外添加 "latest" 标签
            if (config.latest) {
                tags.add("latest")
            }
            val pushTaskName = getTaskName("pushMultiArchFlavorDockerImage", config.targetName, enums)
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
    return namePrefix + "-" + targetName + stringEnums.joinToString("") { "-" + it.value }
}
