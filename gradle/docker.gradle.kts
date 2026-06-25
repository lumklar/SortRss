import buildlogic.constant.*
import buildlogic.docker.*
import buildlogic.flavors.*
import buildlogic.utils.getConfigString

// 1. 读取配置
val dockerRegistry = getConfigString(PropertiesContant.DOCKER_REGISTRY, "")
val dockerNamespace = getConfigString(PropertiesContant.DOCKER_NAMESPACE, "lumklar")
val dockerImageName = getConfigString(PropertiesContant.DOCKER_IMAGE_NAME, "sortrss")
val projectVersion = project.version.toString()
val imageNamePrefix = buildImageNamePrefix(dockerNamespace, dockerImageName, projectVersion)

// 2. 定义配置列表（保持不变）
val dockerTaskConfigs = listOf(
    DockerTaskConfig(
        dockerFileRelativePath = "jvm-with-frontend",
        suffix = "jvm-wasmJs",
        buildArgs = mapOf("DIST_ROOT" to "wasmJs/productionExecutable"),
        tagAsGlobalLatest = true,
        dependencies = listOf(":server:bootJar", ":app:webApp:wasmJsBrowserDistribution"),
        flavors = FlavorCombination(DataFlavor.NETWORK)
    ),
)

// 3. 注册基础镜像任务
dockerTaskConfigs.forEach { config ->
    createLatestDockerTask(
        dockerFileRelativePath = config.dockerFileRelativePath,
        suffix = config.suffix,
        imageTagPrefix = imageNamePrefix,   // 无 Registry
        buildArgs = config.buildArgs,
        tagAsGlobalLatest = config.tagAsGlobalLatest,
        dependencies = config.dependencies.toTypedArray()
    )
}

// 4. 创建风味包装任务（传入 dockerRegistry 用于多 Registry）
val (wrapperNames, pushNames) = createFlavorWrapperTasks(
    configs = dockerTaskConfigs,
    imageNamePrefix = imageNamePrefix,
    dockerRegistry = dockerRegistry,
    dockerNamespace = dockerNamespace,
    dockerImageName = dockerImageName
)

// 5. 聚合任务
tasks.register("buildAllFlavorLatestDockerImage") {
    group = "docker"
    description = "Build all latest Docker images for all configured flavor combinations"
    dependsOn(wrapperNames)
}

tasks.register("pushAllFlavorLatestDockerImage") {
    group = "docker"
    description = "Push all latest Docker images for all configured flavor combinations"
    dependsOn(pushNames)
}