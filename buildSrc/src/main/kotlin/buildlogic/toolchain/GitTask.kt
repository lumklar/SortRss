package buildlogic.toolchain

import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import org.gradle.process.ExecOperations
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import javax.inject.Inject

@CacheableTask
internal abstract class GitArchiveUnpackTask @Inject constructor(
    private val execOps: ExecOperations
) : DefaultTask() {

    @get:Input
    abstract val branch: Property<String>          // Git 分支名

    @get:Input
    abstract val workingDir: Property<String>     // Git 仓库根目录

    @get:Input
    abstract val deployPrefix: Property<String>

    @get:LocalState                               // 临时文件，不作为缓存输出
    abstract val archiveFile: RegularFileProperty  // 生成的 .tar.gz 文件路径

    @get:OutputDirectory
    abstract val unpackDir: DirectoryProperty      // 解压目标目录

    @TaskAction
    fun archiveAndUnpack() {
        val workDir = File(workingDir.get())
        val branchName = branch.get()
        val tarFile = archiveFile.get().asFile
        val outputDir = unpackDir.get().asFile

        // 清理旧的解压目录（如果有）
        if (outputDir.exists()) {
            outputDir.deleteRecursively()
            if (outputDir.exists()) {
                logger.warn("Failed to delete old output directory: ${outputDir.absolutePath}")
            }
        }

        // 创建必要目录
        outputDir.mkdirs()
        tarFile.parentFile.mkdirs()

        // 构建 git archive 命令，若 deployPrefix 非空则附加路径
        val cmd = mutableListOf("git", "archive", "--format=tar.gz", "-o", tarFile.absolutePath, branchName)
        val prefix = deployPrefix.getOrElse("").trim('/')   // 去除首尾斜杠，避免路径错误
        if (prefix.isNotEmpty()) {
            cmd.add(prefix)
        }

        logger.quiet("Executing: ${cmd.joinToString(" ")} in ${workDir.absolutePath}")
        // 执行 git archive，直接输出 gzip 压缩的 tar
        execOps.exec {
            workingDir = workDir
            commandLine(cmd)
        }

        // 构建剥离前缀（如果 deployPrefix 非空）
        val stripPrefix = if (deployPrefix.isPresent && deployPrefix.get().isNotEmpty()) {
            deployPrefix.get().trim('/') + "/"
        } else null

        // 解压 tar.gz 文件（跨平台）
        unpackTarGz(tarFile, outputDir, stripPrefix)

        // 主动删除临时文件（可选，@LocalState 也会在清理阶段删除）
        tarFile.delete()
    }

    private fun unpackTarGz(tarGzFile: File, destDir: File, stripPrefix: String? = null) {
        GzipCompressorInputStream(BufferedInputStream(FileInputStream(tarGzFile))).use { gzIn ->
            TarArchiveInputStream(gzIn).use { tis ->
                var entry = tis.nextEntry
                while (entry != null) {
                    if (entry.isPaxHeader || entry.isGlobalPaxHeader) {
                        entry = tis.nextEntry
                        continue
                    }
                    var entryName = entry.name
                    // 如果指定了前缀并且当前条目以该前缀开头，则去掉前缀
                    if (stripPrefix != null && entryName.startsWith(stripPrefix)) {
                        entryName = entryName.substring(stripPrefix.length)
                        // 如果去掉前缀后为空（例如条目正好是 "docs/"），则跳过该条目
                        if (entryName.isEmpty()) {
                            entry = tis.nextEntry
                            continue
                        }
                    }
                    val targetFile = File(destDir, entryName)
                    if (entry.isDirectory) {
                        targetFile.mkdirs()
                    } else {
                        targetFile.parentFile.mkdirs()
                        targetFile.outputStream().use { os ->
                            tis.transferTo(os)
                        }
                    }
                    entry = tis.nextEntry
                }
            }
        }
    }
}