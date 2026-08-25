package buildlogic.toolchain

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import javax.inject.Inject

@CacheableTask
internal abstract class PipInstallTask @Inject constructor(
    private val execOps: ExecOperations
) : DefaultTask() {

    @get:InputFile
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val requirementsFile: RegularFileProperty

    @TaskAction
    fun install() {
        val reqFile = requirementsFile.get().asFile
        execOps.exec {
            workingDir = reqFile.parentFile
            commandLine("python", "-m", "pip", "install", "-r", reqFile.name)
        }
    }
}
