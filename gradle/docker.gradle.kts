import buildlogic.constant.*
import buildlogic.docker.*
import buildlogic.flavors.*
import buildlogic.utils.getConfigString
import kotlin.String
import kotlin.collections.List

//TODO多个同模块任务并行是不是会出错

// 1. 读取配置
val dockerRegistry = getConfigString(PropertiesContant.DOCKER_REGISTRY, "")
val dockerNamespace = getConfigString(PropertiesContant.DOCKER_NAMESPACE, "lumklar")
val dockerImageName = getConfigString(PropertiesContant.DOCKER_IMAGE_NAME, "sortrss")
val projectVersion = project.version.toString()

// 2. 定义配置列表（保持不变）
createDockerBuildTask(
    taskName = "buildJvmWasmJs",
    dockerfileDir = "scripts/docker/jvm-with-frontend",
    namespace = dockerNamespace,
    projectName = dockerImageName,
    imageName = "wasmjs",
    imageVersion = projectVersion,
    envVars = mapOf("VERSION" to projectVersion),
    dependencies = listOf(":server" to "bootJar", ":app:webApp" to "wasmJsBrowserDistribution")
)

createDockerBuildTask(
    taskName = "buildJvmWasmJsNetwork",
    dockerfileDir = "scripts/docker/jvm-with-frontend",
    namespace = dockerNamespace,
    projectName = dockerImageName,
    imageName = "wasmjs",
    imageVersion = projectVersion,
    envVars = mapOf("VERSION" to projectVersion),
    dependencies = listOf(":server" to "bootJar", ":app:webApp" to "wasmJsBrowserDistribution"),
    stringEnums = listOf(DataFlavor.NETWORK)
)
