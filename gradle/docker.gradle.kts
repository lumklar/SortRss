import getConfigString

val dockerRegistry = getConfigString("docker.registry", "ghcr.io")
val dockerNamespace = getConfigString("docker.namespace", "lumklar")
val dockerImageName = getConfigString("docker.imageName", "sortrss")
val projectVersion = project.version.toString()
val imageNamePrefix = "${dockerRegistry}/${dockerNamespace}/${dockerImageName}:${projectVersion}-"

data class DockerTaskConfig(
    val dockerFileRelativePath: String,
    val suffix: String,          // null 表示使用默认逻辑
    val buildArgs: Map<String, String> = emptyMap(),
    val tagAsGlobalLatest: Boolean = false,
    val dependencies: Array<String> = emptyArray()
)

val dockerTaskConfigs = listOf(
    DockerTaskConfig(
        dockerFileRelativePath = "jvm-with-frontend",
        suffix = "jvm-wasmJs",
        buildArgs = mapOf("DIST_ROOT" to "wasmJs/productionExecutable"),
        tagAsGlobalLatest = true,
        dependencies = arrayOf(":server:bootJar", ":app:webApp:wasmJsBrowserDistribution")
    ),
)

/**
 * 注册一个 Docker 构建任务
 *
 * @param taskName       任务名称，如 "dockerBuildJvmOnly"
 * @param dockerFileDir  Dockerfile 所在目录，相对项目根目录，如 "deploy/docker/jvm-only"
 * @param buildArgs      构建参数 Map，如 mapOf("VERSION" to version, "DIST_ROOT" to "js/productionExecutable")
 * @param imageTag       完整镜像标签，如 "ghcr.io/lumklar/sortrss:1.0-with-js"
 * @param dependencies   前置依赖任务路径，如 ":server:build", ":app:webApp:build"
 */
fun createDockerBuildTask(
    taskName: String,
    dockerFileDir: String,
    buildArgs: Map<String, String>,
    imageTag: String,
    extraTags: List<String> = emptyList(),
    vararg dependencies: String
) {
    tasks.register(taskName, Exec::class) {
        group = "docker"
        description = "Build Docker image using $dockerFileDir/Dockerfile"

        // 添加所有依赖
        dependsOn(dependencies.toList())

        // 构建 docker build 命令
        val cmd = mutableListOf(
            "docker",
            "build",
            "--file", "$dockerFileDir/Dockerfile",
            "--tag", imageTag
        )
        extraTags.forEach { extraTag ->
            cmd.addAll(listOf("--tag", extraTag))
        }
        buildArgs.forEach { (key, value) ->
            cmd.addAll(listOf("--build-arg", "$key=$value"))
        }
        cmd.add(".") // 构建上下文为项目根

        commandLine(cmd)

        doFirst {
            println("Executing: ${commandLine.joinToString(" ")}")
        }
    }
}

/**
 * 便捷方法：根据标签后缀自动生成 Docker 构建任务
 *
 * @param suffix                标签后缀（不带前导 "-"），例如 "with-js"、"jvm-only"
 *                               最终镜像标签为 [imageNamePrefix] + suffix
 * @param dockerFileRelativePath Dockerfile 相对于 "deploy/docker" 的路径，例如 "jvm-only"、"jvm-with-frontend"
 * @param buildArgs             额外的构建参数（会自动合并 VERSION=projectVersion）
 * @param dependencies          前置任务依赖（任意数量，如 ":server:build"、":app:webApp:build"）
 */
fun createDockerTask(
    dockerFileRelativePath: String,
    tagSuffix: String,
    taskSuffix: String = tagSuffix,
    buildArgs: Map<String, String> = emptyMap(),
    tagAsLatestVariant: Boolean = true,   // 打上 latest-<suffix>
    tagAsGlobalLatest: Boolean = false,   // 打上 latest
    vararg dependencies: String
) {
    val taskName = "buildDockerImage-$taskSuffix"
    val dockerFileDir = "deploy/docker/$dockerFileRelativePath"
    val fullBuildArgs = mapOf("VERSION" to projectVersion) + buildArgs
    val imageTag = imageNamePrefix + tagSuffix   // imageNamePrefix 末尾已有 "-"

    val extraTags = mutableListOf<String>()
    if (tagAsLatestVariant) {
        val variantLatest = "${dockerRegistry}/${dockerNamespace}/${dockerImageName}:latest-$tagSuffix"
        extraTags.add(variantLatest)
    }
    if (tagAsGlobalLatest) {
        val globalLatest = "${dockerRegistry}/${dockerNamespace}/${dockerImageName}:latest"
        extraTags.add(globalLatest)
    }

    createDockerBuildTask(
        taskName = taskName,
        dockerFileDir = dockerFileDir,
        buildArgs = fullBuildArgs,
        imageTag = imageTag,
        dependencies = dependencies
    )
}

/**
 * 便捷方法：根据标签后缀自动生成 Docker 构建任务
 *
 * @param suffix                标签后缀（不带前导 "-"），例如 "with-js"、"jvm-only"
 *                               最终镜像标签为 [imageNamePrefix] + suffix
 * @param dockerFileRelativePath Dockerfile 相对于 "deploy/docker" 的路径，例如 "jvm-only"、"jvm-with-frontend"
 * @param buildArgs             额外的构建参数（会自动合并 VERSION=projectVersion）
 * @param dependencies          前置任务依赖（任意数量，如 ":server:build"、":app:webApp:build"）
 */
fun createDockerTask(
    dockerFileRelativePath: String,
    // 默认取目录名
    suffix: String,
    buildArgs: Map<String, String> = emptyMap(),
    tagAsLatestVariant: Boolean = true,   // 打上 latest-<suffix>
    tagAsGlobalLatest: Boolean = false,   // 打上 latest
    vararg dependencies: String
) {
    createDockerTask(
        dockerFileRelativePath = dockerFileRelativePath,
        taskSuffix = suffix,
        tagSuffix = suffix,
        buildArgs = buildArgs,
        tagAsLatestVariant = tagAsLatestVariant,   // 打上 latest-<suffix>
        tagAsGlobalLatest = tagAsGlobalLatest,   // 打上 latest
        dependencies = dependencies
    )
}

fun createLatestDockerTask(
    dockerFileRelativePath: String,
    suffix: String,
    buildArgs: Map<String, String> = emptyMap(),
    tagAsGlobalLatest: Boolean = false,   // 打上 latest
    vararg dependencies: String
) {
    createDockerTask(
        dockerFileRelativePath = dockerFileRelativePath,
        taskSuffix = suffix + "-latest",
        tagSuffix = suffix,
        buildArgs = buildArgs,
        tagAsLatestVariant = true,
        tagAsGlobalLatest = tagAsGlobalLatest,
        dependencies = dependencies,
    )
}

fun createDockerTasks(
    dockerFileRelativePath: String,
    suffix: String,
    buildArgs: Map<String, String> = emptyMap(),
    tagAsGlobalLatest: Boolean = false,   // 打上 latest
    vararg dependencies: String
) {
    createDockerTask(
        dockerFileRelativePath = dockerFileRelativePath,
        suffix = suffix,
        buildArgs = buildArgs,
        tagAsLatestVariant = false,
        tagAsGlobalLatest = tagAsGlobalLatest,
        dependencies = dependencies,
    )

    createLatestDockerTask(
        dockerFileRelativePath = dockerFileRelativePath,
        suffix = suffix,
        buildArgs = buildArgs,
        tagAsGlobalLatest = tagAsGlobalLatest,
        dependencies = dependencies,
    )
}

dockerTaskConfigs.forEach { config ->
    createLatestDockerTask(
        dockerFileRelativePath = config.dockerFileRelativePath,
        suffix = config.suffix,
        buildArgs = config.buildArgs,
        tagAsGlobalLatest = config.tagAsGlobalLatest,
        dependencies = *config.dependencies
    )
}

tasks.register("buildAllLatestDockerImage") {
    group = "docker"
    description = "Build all latest Docker images (with 'latest-<suffix>' tags)"

    // 依赖所有通过 createLatestDockerTask 生成的任务
    // 任务命名规则：buildDockerImage-<suffix>-latest
    dependsOn(dockerTaskConfigs.map { "buildDockerImage-${it.suffix}-latest" })
}
