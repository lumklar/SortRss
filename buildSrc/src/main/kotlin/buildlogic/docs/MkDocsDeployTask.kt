package buildlogic.docs

// buildSrc/src/main/kotlin/MikeDeployTask.kt
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import javax.inject.Inject

@CacheableTask
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

    @TaskAction
    fun deploy() {
        //TODO 推送到远端
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
                val cmd = mutableListOf(
                    "mike", "deploy",
                    "-b", branchVal,
                    "--deploy-prefix", prefixVal
                )
                if (doPush) cmd.add("--push")
                cmd.add(version)
                commandLine(cmd)
            }
        }

        // 设置默认版本
        execOps.exec {
            workingDir = workDir
            val cmd = mutableListOf(
                "mike", "set-default",
                "-b", branchVal,
                "--deploy-prefix", prefixVal
            )
            if (doPush) cmd.add("--push")
            cmd.add(defaultVal)
            commandLine(cmd)
        }
    }
}
