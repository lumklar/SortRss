import buildlogic.constant.*
import buildlogic.docker.*
import buildlogic.flavors.*
import buildlogic.utils.getConfigString

//TODO多个同模块任务并行是不是会出错

// 1. 读取配置
val dockerRegistry = getConfigString(PropertiesContant.DOCKER_REGISTRY, "")
val dockerNamespace = getConfigString(PropertiesContant.DOCKER_NAMESPACE, "lumklar")
val dockerRepository = getConfigString(PropertiesContant.DOCKER_REPOSITORY, "sortrss")
val projectVersion = project.version.toString()

// 2. 定义配置列表
createDockerTask(
    listOf(
        DockerConfig(
            targetName = "jvm-wasmJs",
            //TODO 改为文件名而不是目录？
            dockerfileDir = "scripts/docker/jvm-with-frontend",
            namespace = dockerNamespace,
            repository = dockerRepository,
            registryList = listOf(dockerRegistry),
            envVars = mapOf("VERSION" to projectVersion),
            dependencies = listOf(":server" to "bootJar", ":app:webApp" to "wasmJsBrowserDistribution"),
            stringEnums = listOf(listOf(DataFlavor.NETWORK)),
            platforms = listOf("linux/amd64", "linux/arm64", "windows/amd64", "linux/ppc64le", "linux/s390x"),
            versionLatest = true,
            latest = true
        )
    )
)
