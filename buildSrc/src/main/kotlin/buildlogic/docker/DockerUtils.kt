package buildlogic.docker

import buildlogic.flavors.StringEnum
import buildlogic.utils.NameUtils
import org.gradle.api.GradleException
import java.io.File

private val DOCKER_TAG_REGEX = Regex("^[a-z0-9][a-z0-9._-]{0,127}$")

/**
 * 统一校验并规范化 Docker 标签片段。
 *
 * 项目版本和 DOCKER_IMAGE_VERSION 都走这里，避免常规版本走 NameUtils.validateVersion、
 * 环境变量却直接拼进 tag 而产生 `dev/foo`、`..` 等非法镜像名。
 */
internal fun validateDockerTagPart(value: String, source: String): String {
    val normalized = value.trim().lowercase().replace('_', '-')
    require(normalized.matches(DOCKER_TAG_REGEX)) {
        "Invalid Docker tag from $source: '$value'. Expected [a-z0-9][a-z0-9._-]{0,127}"
    }
    return normalized
}

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
    val normalizedVersion = validateDockerTagPart(imageVersion, "image version")
    val tagSuffix =
        NameUtils.toKebabCase(targetName + stringEnums.joinToString(separator = "") { "-${it.value.lowercase()}" })
    val tag = "$normalizedVersion-$tagSuffix"
    require(tag.matches(DOCKER_TAG_REGEX)) {
        "Invalid Docker tag: '$tag'. Expected [a-z0-9][a-z0-9._-]{0,127}"
    }
    return tag
}
