package buildlogic.docs

// buildSrc/src/main/kotlin/MikeDeployTask.kt
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import javax.inject.Inject

/**
 * 通过 mike 把当前文档版本部署到 `docs` 分支。
 *
 * 该任务会写 Git 分支、且 `push=true` 时会推送到远端，属于有副作用的外部命令任务，
 * 因此不能用 `@CacheableTask`：Gradle 一旦从构建缓存恢复该任务，`--push` 命令不会执行，
 * 表现为“push 参数存在却没推到远端”。这里显式禁用 up-to-date 检查，保证每次调用都执行。
 */
internal abstract class MikeDeployTask @Inject constructor(
    private val execOps: ExecOperations
) : DefaultTask() {

    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val workingDir: DirectoryProperty

    @get:Input
    abstract val branch: Property<String>

    @get:Input
    abstract val deployPrefix: Property<String>

    @get:Input
    abstract val versions: ListProperty<String>

    @get:Input
    abstract val defaultVersion: Property<String>

    @get:Input
    abstract val push: Property<Boolean>

    init {
        // 有外部副作用（写 docs 分支 / 推送远端），不允许被判定为 UP-TO-DATE
        outputs.upToDateWhen { false }
    }

    @TaskAction
    fun deploy() {
        val workDir = workingDir.get().asFile
        val branchVal = branch.get()
        val prefixVal = deployPrefix.get()
        val versionList = versions.get()
        val defaultVal = defaultVersion.get()
        val doPush = push.get()

        require(workDir.exists()) { "Working directory '${workDir.absolutePath}' does not exist" }
        require(versionList.isNotEmpty()) { "Versions list must not be empty" }
        require(defaultVal in versionList) { "defaultVersion '$defaultVal' not found in versions list: $versionList" }

        // 为每个版本执行 mike deploy
        versionList.forEach { version ->
            execOps.exec {
                workingDir = workDir
                val cmd = buildList {
                    addAll(listOf("mike", "deploy", "-b", branchVal, "--deploy-prefix", prefixVal))
                    if (doPush) add("--push")
                    add(version)
                }
                commandLine(cmd)
            }
        }

        // 设置默认版本
        execOps.exec {
            workingDir = workDir
            val cmd = buildList {
                addAll(listOf("mike", "set-default", "-b", branchVal, "--deploy-prefix", prefixVal))
                if (doPush) add("--push")
                add(defaultVal)
            }
            commandLine(cmd)
        }
    }
}
