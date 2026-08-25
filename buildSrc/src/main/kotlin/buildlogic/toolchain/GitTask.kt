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
abstract class GitArchiveUnpackTask @Inject constructor(
    private val execOps: ExecOperations
) : DefaultTask() {

    @get:Input
    abstract val branch: Property<String>          // Git 分支名

    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val workingDir: DirectoryProperty     // Git 仓库根目录

    @get:LocalState                               // 临时文件，不作为缓存输出
    abstract val archiveFile: RegularFileProperty  // 生成的 .tar.gz 文件路径

    @get:OutputDirectory
    abstract val unpackDir: DirectoryProperty      // 解压目标目录

    @TaskAction
    fun archiveAndUnpack() {
        val workDir = workingDir.get().asFile
        val branchName = branch.get()
        val tarFile = archiveFile.get().asFile
        val outputDir = unpackDir.get().asFile

        // 创建必要目录
        outputDir.mkdirs()
        tarFile.parentFile.mkdirs()

        // 执行 git archive，直接输出 gzip 压缩的 tar
        execOps.exec {
            workingDir = workDir
            commandLine("git", "archive", "--format=tar.gz", "-o", tarFile.absolutePath, branchName)
        }

        // 解压 tar.gz 文件（跨平台）
        unpackTarGz(tarFile, outputDir)

        // 主动删除临时文件（可选，@LocalState 也会在清理阶段删除）
        tarFile.delete()
    }

    private fun unpackTarGz(tarGzFile: File, destDir: File) {
        // 先用 GzipCompressorInputStream 解压缩，再用 TarArchiveInputStream 解析 tar
        GzipCompressorInputStream(BufferedInputStream(FileInputStream(tarGzFile))).use { gzIn ->
            TarArchiveInputStream(gzIn).use { tis ->
                var entry = tis.nextEntry
                while (entry != null) {
                    val targetFile = File(destDir, entry.name)
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