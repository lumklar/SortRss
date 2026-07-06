package buildlogic.docker

import buildlogic.flavors.StringEnum
import org.gradle.api.GradleException
import java.io.File

/**
 * 构建 docker build 命令及镜像全名，并检查 Dockerfile 目录是否存在。
 * @return Pair<镜像全名, 命令列表>
 */
internal fun buildDockerCommand(
    dockerfileDir: File,
    namespace: String,
    repository: String,
    targetName: String,
    imageVersion: String,
    envVars: Map<String, String>,
    stringEnums: List<StringEnum> = emptyList()
): Pair<String, List<String>> {
    if (!dockerfileDir.exists()) {
        throw GradleException("Dockerfile directory does not exist: ${dockerfileDir.absolutePath}")
    }
    val fullImageName = "$namespace/$repository:" + buildDockerTag(
        imageVersion = imageVersion,
        targetName = targetName,
        stringEnums = stringEnums
    )
    val command = mutableListOf<String>().apply {
        add("docker")
        add("build")
        add("-t")
        add(fullImageName)
        add("-f")
        add("${dockerfileDir.absolutePath}/Dockerfile")  // 假定 Dockerfile 直接位于该目录下
        envVars.forEach { (key, value) ->
            add("--build-arg")
            add("$key=$value")
        }
        add(dockerfileDir.absolutePath)  // 上下文路径
    }
    return fullImageName to command
}

internal fun buildDockerTag(
    imageVersion: String,
    targetName: String,
    stringEnums: List<StringEnum> = emptyList()
): String {
    return imageVersion + "-" + targetName + stringEnums.joinToString(separator = "") { "-${it.value.lowercase()}" }
}