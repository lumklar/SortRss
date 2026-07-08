package buildlogic.docker

import buildlogic.flavors.StringEnum
import buildlogic.utils.NameUtils
import org.gradle.api.GradleException
import org.gradle.internal.impldep.org.eclipse.jgit.lib.ObjectChecker.tag
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
    var tagSuffix =
        NameUtils.toKebabCase(targetName + stringEnums.joinToString(separator = "") { "-${it.value.lowercase()}" })
    //TODO version有两种一种是常规的，一种是环境变量引入的，两种校验方式不一致
    return imageVersion + "-" + tagSuffix
}