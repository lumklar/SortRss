package buildlogic.docker

import buildlogic.flavors.StringEnum
import buildlogic.utils.NameUtils
import org.gradle.api.GradleException
import org.gradle.process.ExecOperations
import java.io.File
import java.io.FileOutputStream
import java.nio.channels.FileLock
import java.time.Instant

/**
 * Docker 任务共用的执行逻辑。
 *
 * 并发安全：多个任务可能同时触发相同的依赖构建或写同一个镜像标签，
 * 这里按「依赖任务集合」和「镜像标签」分别加文件锁，避免子进程 Gradle
 * 互相争抢项目锁、以及同一标签被并发覆盖。锁文件落在根项目 build/ 下，
 * 不入库。
 */
internal object DockerTaskSupport {

    /**
     * 通过子 Gradle 进程执行依赖任务，隔离 -P 风味参数。
     */
    fun runDependencyTasks(
        execOps: ExecOperations,
        gradlewPath: String,
        rootDir: File,
        dependencies: List<Pair<String, String>>,
        stringEnums: List<StringEnum>
    ) {
        if (dependencies.isEmpty()) return
        val taskPaths = dependencies.map { (module, task) -> "$module:$task" }
        withFileLock(rootDir, "deps-${taskPaths.sorted().joinToString("_")}") {
            execOps.exec {
                workingDir = rootDir
                commandLine = buildList {
                    add(gradlewPath)
                    addAll(taskPaths)
                    stringEnums.forEach { enum -> add("-P${enum.envKey}=${enum.value}") }
                }
            }
        }
    }

    /**
     * 执行 docker 命令，按涉及到的镜像标签加锁。
     */
    fun runDockerCommand(execOps: ExecOperations, rootDir: File, imageTags: List<String>, command: List<String>) {
        withFileLock(rootDir, "image-${imageTags.sorted().joinToString("_")}") {
            execOps.exec {
                workingDir = rootDir
                commandLine = command
            }
        }
    }

    fun dockerfileIn(dockerfileDir: File): File {
        val dockerfile = File(dockerfileDir, "Dockerfile")
        if (!dockerfile.isFile) {
            throw GradleException("Dockerfile does not exist: ${dockerfile.absolutePath}")
        }
        return dockerfile
    }

    /**
     * 解析镜像版本标签。环境变量优先；未设置时退回项目版本 + 可选的 latest。
     * 环境变量在这里（执行期）读取，而不是在配置脚本里读，便于配置缓存复用。
     */
    fun resolveTags(
        envVersion: String,
        projectVersion: String,
        versionLatest: Boolean,
        extraLatest: Boolean,
        targetName: String,
        stringEnums: List<StringEnum>
    ): List<String> {
        val fromEnv = envVersion.split(',')
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .map { NameUtils.toKebabCase(it) }
        if (fromEnv.isNotEmpty()) {
            return fromEnv.distinct().map { buildDockerTag(it, targetName, stringEnums) }
        }

        val versions = buildList {
            add(projectVersion.lowercase())
            if (versionLatest) add("latest")
        }
        val tags = versions.map { buildDockerTag(it, targetName, stringEnums) }.toMutableList()
        if (extraLatest) tags.add("latest")
        return tags.distinct()
    }

    /**
     * 平台列表同样在执行期解析；未设置时退回 DockerConfig 中的声明。
     */
    fun resolvePlatforms(envPlatforms: String, configured: List<String>): List<String> {
        val fromEnv = envPlatforms.split(',')
            .map { it.trim() }
            .filter { it.isNotBlank() }
        return fromEnv.ifEmpty { configured }.distinct()
    }

    /**
     * 把 "linux/amd64" 转成可安全用于任务名和镜像标签的后缀。
     */
    fun platformTagSuffix(platform: String): String {
        return platform.trim().lowercase().replace(Regex("[^a-z0-9._-]+"), "-")
    }

    /**
     * 生成 buildx 构建推送命令，供多架构任务和分架构任务共用。
     */
    fun buildxBuildCommand(
        dockerfile: File,
        contextDir: File,
        platforms: String,
        imageTags: List<String>,
        envVars: Map<String, String>,
        imageVersion: String,
        metadataFile: File? = null
    ): List<String> {
        return buildList {
            add("docker")
            add("buildx")
            add("build")
            add("--platform")
            add(platforms)
            add("--push")
            imageTags.forEach { tag ->
                add("--tag")
                add(tag)
            }
            envVars.forEach { (key, value) ->
                add("--build-arg")
                add("$key=$value")
            }
            add("--label")
            add("org.opencontainers.image.version=$imageVersion")
            add("--label")
            add("org.opencontainers.image.created=${Instant.now()}")
            metadataFile?.let {
                add("--metadata-file")
                add(it.absolutePath)
            }
            add("-f")
            add(dockerfile.absolutePath)
            add(contextDir.absolutePath)
        }
    }

    private fun withFileLock(rootDir: File, key: String, action: () -> Unit) {
        val lockDir = File(rootDir, "build/docker-locks")
        lockDir.mkdirs()
        val safeKey = key.lowercase().replace(Regex("[^a-z0-9._-]+"), "-")
        val lockFile = File(lockDir, "$safeKey.lock")
        FileOutputStream(lockFile).use { stream ->
            val lock: FileLock = stream.channel.lock()
            try {
                action()
            } finally {
                lock.release()
            }
        }
    }
}
