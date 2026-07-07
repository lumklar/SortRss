package buildlogic.docker

import buildlogic.flavors.StringEnum
import org.gradle.api.GradleException
import java.io.File

/**
 * 构建 docker build 命令及镜像全名，并检查 Dockerfile 目录是否存在。
 * @return Pair<镜像全名, 命令列表>
 */
internal fun buildDockerCommand(
    projectDir: File,                // 新增参数
    dockerfileDir: File,
    namespace: String,
    repository: String,
    targetName: String,
    imageVersion: String,
    envVars: Map<String, String>,
    stringEnums: List<StringEnum> = emptyList()
): Pair<String, List<String>> {
    val dockerfile = File(dockerfileDir, "Dockerfile")
    if (!dockerfile.exists()) {
        throw GradleException("Dockerfile does not exist: ${dockerfile.absolutePath}")
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
        add(dockerfile.absolutePath)   // Dockerfile 用绝对路径指定
        envVars.forEach { (key, value) ->
            add("--build-arg")
            add("$key=$value")
        }
        add(projectDir.absolutePath)   // ★ 上下文改为项目根目录
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