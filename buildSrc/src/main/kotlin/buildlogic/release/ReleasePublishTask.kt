package buildlogic.release

import buildlogic.flavors.StringEnum
import org.apache.commons.compress.archivers.tar.TarArchiveEntry
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files
import javax.inject.Inject

/**
 * 发布产物任务：执行指定模块的 Gradle 任务 → 复制或打包产物到 build/release
 *
 * 所有输入属性在配置阶段设置，执行阶段不访问 Project 对象，兼容 Gradle 配置缓存。
 */
abstract class ReleasePublishTask @Inject constructor(
    private val execOps: ExecOperations   // 注入执行外部命令的服务
) : DefaultTask() {

    // ---------- 输入属性（全部 @Input，用于配置缓存和增量构建） ----------

    /** gradlew 脚本的绝对路径（根项目下的 gradlew 或 gradlew.bat） */
    @get:Input
    abstract val gradlewPath: Property<String>

    /** 目标模块的项目根目录（用于定位模块和设置 workingDir） */
    @get:Input
    abstract val targetProjectDir: Property<File>

    /** 目标模块的 build 目录（用于定位产物） */
    @get:Input
    abstract val targetBuildDir: Property<File>

    /** 产物相对目标模块 build 目录的路径（如 "libs/app.jar"） */
    @get:Input
    abstract val artifactRelativePath: Property<String>

    /** 最终文件名（不含扩展名，打包时会自动加 .tar.gz） */
    @get:Input
    abstract val renameTo: Property<String>

    /** 是否打包为 tar.gz（true：打包，false：仅复制） */
    @get:Input
    abstract val shouldPackage: Property<Boolean>

    /** 环境变量列表（会传递给执行的 Gradle 任务） */
    @get:Input
    abstract val envVars: ListProperty<StringEnum>

    /** 最终输出目录（通常是当前项目的 build/release） */
    @get:Input
    abstract val destinationDir: Property<File>

    /** 目标模块中要执行的 Gradle 任务名（如 "build"） */
    @get:Input
    abstract val moduleTask: Property<String>

    @TaskAction
    fun publish() {
        // ========== 1. 执行目标模块的 Gradle 任务 ==========
        // 使用 execOps.exec 执行外部命令（gradlew），并传入环境变量
        execOps.exec {
            // 工作目录设为目标模块根目录（让 gradlew 能正确解析模块路径）
            workingDir = targetProjectDir.get()
            // 命令：gradlew -p 项目目录 任务名
            commandLine = listOf(
                gradlewPath.get(),
                "-p", targetProjectDir.get().absolutePath,
                moduleTask.get()
            )
            // 继承当前进程的所有环境变量，并叠加自定义的
            environment = System.getenv() + envVars.get().associate { it.envKey to it.value }
        }

        // ========== 2. 定位产物 ==========
        // 拼接出产物文件（可能为目录）
        val sourceFile = targetBuildDir.get().resolve(artifactRelativePath.get())
        if (!sourceFile.exists()) {
            throw GradleException("产物不存在: ${sourceFile.absolutePath}")
        }

        // 确保目标目录存在
        val destDir = destinationDir.get()
        destDir.mkdirs()

        // 确定最终文件名（若未指定重命名，则使用源文件名）
        val baseName = renameTo.getOrElse(sourceFile.name)

        if (shouldPackage.get()) {
            // ========== 3a. 打包为 tar.gz（不复制，直接打包） ==========
            val tarFile = destDir.resolve("$baseName.tar.gz")
            // 使用 Apache Commons Compress 生成 tar.gz
            FileOutputStream(tarFile).use { fos ->
                BufferedOutputStream(fos).use { bos ->
                    GzipCompressorOutputStream(bos).use { gos ->
                        TarArchiveOutputStream(gos).use { tarOut ->
                            // 支持长文件名（POSIX 标准）
                            tarOut.setLongFileMode(TarArchiveOutputStream.LONGFILE_POSIX)

                            // 递归添加目录或单个文件
                            fun addFile(file: File, entryName: String = file.name) {
                                val entry = TarArchiveEntry(file, entryName)
                                entry.size = file.length()
                                tarOut.putArchiveEntry(entry)
                                if (file.isFile) {
                                    Files.copy(file.toPath(), tarOut)
                                }
                                tarOut.closeArchiveEntry()
                            }

                            if (sourceFile.isDirectory) {
                                // 遍历整个目录树
                                sourceFile.walkTopDown().forEach { child ->
                                    // 计算相对路径（相对于 sourceFile）
                                    val relative = sourceFile.toURI().relativize(child.toURI()).path
                                    if (child.isDirectory) {
                                        // 目录项：以 '/' 结尾
                                        val dirEntry = TarArchiveEntry(child, relative + "/")
                                        tarOut.putArchiveEntry(dirEntry)
                                        tarOut.closeArchiveEntry()
                                    } else {
                                        // 普通文件：添加文件内容
                                        val entry = TarArchiveEntry(child, relative)
                                        entry.size = child.length()
                                        tarOut.putArchiveEntry(entry)
                                        Files.copy(child.toPath(), tarOut)
                                        tarOut.closeArchiveEntry()
                                    }
                                }
                            } else {
                                // 单个文件
                                addFile(sourceFile)
                            }
                            tarOut.finish()   // 完成写入
                        }
                    }
                }
            }
            logger.lifecycle("打包产物: ${tarFile.absolutePath}")

        } else {
            // ========== 3b. 复制产物到目标目录（可选重命名） ==========
            val destFile = destDir.resolve(baseName)
            if (sourceFile.isDirectory) {
                // 递归复制目录
                sourceFile.walkTopDown().forEach { src ->
                    val relative = sourceFile.toURI().relativize(src.toURI()).path
                    val target = destFile.resolve(relative)
                    if (src.isDirectory) {
                        target.mkdirs()
                    } else {
                        // 复制文件（若目标父目录不存在，先创建）
                        target.parentFile.mkdirs()
                        Files.copy(src.toPath(), target.toPath())
                    }
                }
            } else {
                // 复制单个文件
                Files.copy(sourceFile.toPath(), destFile.toPath())
            }
            logger.lifecycle("产物已复制到: ${destFile.absolutePath}")
        }
    }
}