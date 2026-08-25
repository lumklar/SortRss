package buildlogic.docs

import buildlogic.toolchain.PipInstallTask
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.register
import java.io.File

// 定义一个包装类，方便外部同时访问两个任务
class MikeDeployTasks(
    val pipInstall: TaskProvider<out DefaultTask>,
    val mikeDeploy: TaskProvider<out DefaultTask>
)

fun Project.createMikeDeployTask(config: MikeDeployConfig): MikeDeployTasks {
    val workingDirFile = file(config.workingDir)
    val reqFile = config.requirementsFile?.let { file(it) } ?: File(workingDirFile, "requirements.txt")

    val pipInstallTask = tasks.register<PipInstallTask>("pipInstallForMkDocs") {
        this.requirementsFile.set(reqFile)
    }

    val mikeDeployTask = tasks.register<MikeDeployTask>("mikeDeploy") {
        dependsOn(pipInstallTask)
        this.workingDir.set(workingDirFile)
        this.branch.set(config.branch)
        this.deployPrefix.set(config.deployPrefix)
        this.versions.set(config.versions)
        this.defaultVersion.set(config.defaultVersion)
    }

    return MikeDeployTasks(pipInstallTask, mikeDeployTask)
}
