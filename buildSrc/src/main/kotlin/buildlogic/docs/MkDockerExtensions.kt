package buildlogic.docs

import buildlogic.toolchain.GitArchiveUnpackTask
import buildlogic.toolchain.PipInstallTask
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.register
import java.io.File

// 定义一个包装类，方便外部同时访问两个任务
class MikeDeployTasks(
    val install: TaskProvider<out DefaultTask>,          // 安装依赖
    val deploy: TaskProvider<out DefaultTask>,           // 部署（不推送）
    val deployPush: TaskProvider<out DefaultTask>,       // 部署并推送
    val deployLatest: TaskProvider<out DefaultTask>,     // 仅部署 latest（不推送）
    val unpack: TaskProvider<out DefaultTask>,           // 解压归档
)

fun Project.createMikeDeployTask(config: MikeDeployConfig): MikeDeployTasks {
    val workingDirFile = file(config.workingDir)
    val reqFile = config.requirementsFile?.let { file(it) } ?: File(workingDirFile, "requirements.txt")

    // 计算解压目录（默认 build/mike）
    val unpackDirPath = config.unpackDir ?: layout.buildDirectory.dir("mike").get().asFile.absolutePath

    // 计算归档文件名（不含后缀）：从 deployPrefix 提取最后一段，若为空则使用 "archive"
    val archiveName = config.archiveName ?: config.deployPrefix
        .trimEnd('/')
        .substringAfterLast('/')
        .takeIf { it.isNotEmpty() } ?: "archive"

    // 构建完整归档文件路径：build/tmp/<archiveName>.tar.gz
    val archiveFilePath = layout.buildDirectory.dir("tmp")
        .get()
        .file("$archiveName.tar.gz")
        .asFile
        .absolutePath

    // 1. 安装依赖
    val installTask = tasks.register<PipInstallTask>("installMikeDeps") {
        this.requirementsFile.set(reqFile)
    }

    // 2. 部署（不推送）
    val deployTask = tasks.register<MikeDeployTask>("mikeDeploy") {
        dependsOn(installTask)
        this.workingDir.set(workingDirFile)
        this.branch.set(config.branch)
        this.deployPrefix.set(config.deployPrefix)
        this.versions.set(config.versions)
        this.defaultVersion.set(config.defaultVersion)
        this.push.set(false)
    }

    // 3. 部署并推送
    val deployPushTask = tasks.register<MikeDeployTask>("mikeDeployPush") {
        dependsOn(installTask)
        this.workingDir.set(workingDirFile)
        this.branch.set(config.branch)
        this.deployPrefix.set(config.deployPrefix)
        this.versions.set(config.versions)
        this.defaultVersion.set(config.defaultVersion)
        this.push.set(true)
    }

    // 4. 仅部署 latest 版本（不推送）
    val deployLatestTask = tasks.register<MikeDeployTask>("mikeDeployLatest") {
        dependsOn(installTask)
        this.workingDir.set(workingDirFile)
        this.branch.set(config.branch)
        this.deployPrefix.set(config.deployPrefix)
        this.versions.set(listOf("latest"))
        this.defaultVersion.set("latest")
        this.push.set(false)
    }

    // 5. 解压归档
    val unpackTask = tasks.register<GitArchiveUnpackTask>("mikeUnpackArchive") {
        this.workingDir.set(project.rootProject.projectDir.absolutePath)
        this.branch.set(config.branch)
        this.deployPrefix.set(config.deployPrefix)
        this.archiveFile.set(file(archiveFilePath))
        this.unpackDir.set(file(unpackDirPath))
    }

    // 返回包含任务的包装对象
    return MikeDeployTasks(
        install = installTask,
        deploy = deployTask,
        deployPush = deployPushTask,
        deployLatest = deployLatestTask,
        unpack = unpackTask
    )
}
