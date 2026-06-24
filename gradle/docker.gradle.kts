import buildlogic.docker.*
import buildlogic.flavors.*
import buildlogic.utils.getConfigString

// 1. 读取配置
val dockerRegistry = getConfigString("docker.registry", "ghcr.io")
val dockerNamespace = getConfigString("docker.namespace", "lumklar")
val dockerImageName = getConfigString("docker.imageName", "sortrss")
val projectVersion = project.version.toString()
val imageNamePrefix = "${dockerRegistry}/${dockerNamespace}/${dockerImageName}:${projectVersion}-"

// 2. 定义配置列表（项目特有）
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

// 3. 注册所有基础镜像任务
dockerTaskConfigs.forEach { config ->
    createLatestDockerTask(
        dockerFileRelativePath = config.dockerFileRelativePath,
        suffix = config.suffix,
        imageTagPrefix = imageNamePrefix,
        buildArgs = config.buildArgs,
        tagAsGlobalLatest = config.tagAsGlobalLatest,
        dependencies = config.dependencies.toTypedArray()
    )
}

// 4. 一键创建所有风味包装任务
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
