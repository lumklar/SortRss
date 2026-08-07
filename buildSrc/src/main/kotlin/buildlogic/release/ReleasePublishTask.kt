package buildlogic.release

import buildlogic.flavors.StringEnum
import org.apache.commons.compress.archivers.tar.TarArchiveEntry
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream
import org.apache.commons.compress.compressors.deflate.DeflateCompressorOutputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream
import org.apache.commons.compress.compressors.lzma.LZMACompressorOutputStream
import org.apache.commons.compress.compressors.xz.XZCompressorOutputStream
import org.apache.commons.compress.compressors.zstandard.ZstdCompressorOutputStream
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
import java.io.OutputStream
import java.nio.file.Files
import java.nio.file.Paths
import javax.inject.Inject

/**
 * 发布产物任务：执行指定模块的 Gradle 任务 → 匹配产物（支持 * 通配符）→ 复制或打包到 build/release
 *
 * 所有输入属性在配置阶段设置，执行阶段不访问 Project 对象，兼容 Gradle 配置缓存。
 * 通过文件锁保证同一模块的发布任务不会并发执行，避免增量缓存冲突。
 */
internal abstract class ReleasePublishTask @Inject constructor(
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

    /** 产物相对目标模块 build 目录的路径，支持 * 通配符（如 "libs/app*.jar"） */
    @get:Input
    abstract val artifactRelativePath: Property<String>

    /** 最终文件名（不含扩展名，会自动根据打包/压缩方式添加 .tar, .tar.gz 或 .gz） */
    @get:Input
    abstract val renameTo: Property<String>

    /** 压缩方式，默认 NONE */
    @get:Input
    abstract val compression: Property<CompressionMode>

    /** 环境变量列表（会传递给执行的 Gradle 任务） */
    @get:Input
    abstract val envVars: ListProperty<StringEnum>

    /** 最终输出目录（通常是当前项目的 build/release） */
    @get:Input
    abstract val destinationDir: Property<File>

    /** 目标模块中要执行的 Gradle 任务名（如 "build"） */
    @get:Input
    abstract val moduleTask: Property<String>

    companion object {
        // 按模块目录存储锁，保证同一模块的任务串行
        private val locks = java.util.concurrent.ConcurrentHashMap<String, java.util.concurrent.locks.ReentrantLock>()
    }

    @TaskAction
    fun publish() {
        // ========== 0. 获取模块级文件锁，防止同一模块的多个发布任务并发 ==========
        // 锁文件放在模块 build 目录下，每个模块独立，互不干扰
        val moduleKey = targetBuildDir.get().absolutePath
        val lock = locks.computeIfAbsent(moduleKey) { java.util.concurrent.locks.ReentrantLock() }
        lock.lock()

        try {
            // ========== 1. 执行目标模块的 Gradle 任务 ==========
            execOps.exec {
                workingDir = targetProjectDir.get()
                commandLine = listOf(
                    gradlewPath.get(),
                    "-p", targetProjectDir.get().absolutePath,
                    moduleTask.get()
                )
                environment = System.getenv() + envVars.get().associate { it.envKey to it.value }
            }

            // ========== 2. 按 Glob 模式匹配产物 ==========
            val baseDir = targetBuildDir.get()
            val pattern = artifactRelativePath.get().replace('\\', '/') // 统一使用 '/'
            val matcher = baseDir.toPath().fileSystem.getPathMatcher("glob:$pattern")

            // 收集所有匹配的路径（仅文件或目录）
            val matchedFiles = Files.walk(baseDir.toPath())
                .filter { path ->
                    val relative = baseDir.toPath().relativize(path).toString().replace('\\', '/')
                    matcher.matches(Paths.get(relative)) && (Files.isRegularFile(path) || Files.isDirectory(path))
                }
                .map { it.toFile() }
                .toList()

            if (matchedFiles.isEmpty()) {
                // 找不到匹配时：定位最底层存在的目录，输出其内容（一级）
                val deepestDir = findDeepestExistingDir(baseDir, pattern)
                val errorMsg = if (deepestDir != null) {
                    "找不到匹配模式 '$pattern' 的产物。最底层匹配目录 '${deepestDir}' 的内容：\n" +
                            listDirContents(deepestDir, maxDepth = 1)
                } else {
                    "找不到匹配模式 '$pattern' 的产物，且无法定位任何有效目录。"
                }
                logger.error(errorMsg)
                throw GradleException(errorMsg)
            }

            // 确保目标目录存在
            val destDir = destinationDir.get()
            destDir.mkdirs()

            // 确定输出基础名（不含扩展名）
            val baseName = when {
                renameTo.isPresent -> renameTo.get()
                matchedFiles.size == 1 -> matchedFiles.first().nameWithoutExtension
                else -> throw GradleException(
                    "匹配到多个产物（${matchedFiles.size} 个），必须显式设置 'renameTo' 属性来指定输出文件名（不含扩展名）。"
                )
            }

            // 判断是否需要打包为 tar（多文件或包含目录）
            val needTar = matchedFiles.size > 1 || matchedFiles.any { it.isDirectory }
            val compressionMode = compression.getOrElse(CompressionMode.NONE)

            if (needTar) {
                // 生成 tar 文件名：baseName.tar + 压缩后缀（若无压缩则仅为 .tar）
                val tarSuffix = if (compressionMode == CompressionMode.NONE) "" else compressionMode.suffix
                val outputFile = destDir.resolve("$baseName.tar$tarSuffix")
                FileOutputStream(outputFile).use { fos ->
                    BufferedOutputStream(fos).use { bos ->
                        val compressor = if (compressionMode == CompressionMode.NONE) {
                            bos
                        } else {
                            createCompressorOutputStream(compressionMode, bos)
                        }
                        TarArchiveOutputStream(compressor).use { tarOut ->
                            tarOut.setLongFileMode(TarArchiveOutputStream.LONGFILE_POSIX)
                            matchedFiles.forEach { file ->
                                addFileToTar(tarOut, file, file.name)
                            }
                            tarOut.finish()
                        }
                    }
                }
                logger.lifecycle("打包产物: ${outputFile.absolutePath}")

            } else {
                // 单文件处理
                val source = matchedFiles.first()
                val ext = source.extension
                val fullName = if (ext.isNotEmpty()) "$baseName.$ext" else baseName
                // 单文件压缩时，在完整文件名后追加压缩后缀（若无压缩则为空）
                val outputFile = if (compressionMode == CompressionMode.NONE) {
                    destDir.resolve(fullName)
                } else {
                    destDir.resolve(fullName + compressionMode.suffix)
                }

                if (compressionMode == CompressionMode.NONE) {
                    Files.copy(source.toPath(), outputFile.toPath())
                } else {
                    FileOutputStream(outputFile).use { fos ->
                        BufferedOutputStream(fos).use { bos ->
                            createCompressorOutputStream(compressionMode, bos).use { compressor ->
                                Files.copy(source.toPath(), compressor)
                            }
                        }
                    }
                }
                logger.lifecycle("产物已输出: ${outputFile.absolutePath}")
            }

        } finally {
            lock.unlock()
        }
    }

    /**
     * 根据压缩模式创建对应的 CompressorOutputStream。
     * 若模式为 NONE，则直接返回原输出流（不做压缩）。
     */
    private fun createCompressorOutputStream(mode: CompressionMode, out: OutputStream): OutputStream {
        //TODO 调整压缩参数？
        return when (mode) {
            CompressionMode.NONE -> out
            CompressionMode.GZIP -> GzipCompressorOutputStream(out)
            CompressionMode.BZIP2 -> BZip2CompressorOutputStream(out)
            CompressionMode.XZ -> XZCompressorOutputStream(out)
            CompressionMode.LZMA -> LZMACompressorOutputStream(out)
            CompressionMode.ZSTANDARD -> ZstdCompressorOutputStream(out)
            CompressionMode.DEFLATE -> DeflateCompressorOutputStream(out)
        }
    }

    /**
     * 递归将文件或目录添加到 tar 输出流中
     */
    private fun addFileToTar(tarOut: TarArchiveOutputStream, file: File, entryBaseName: String) {
        if (file.isDirectory) {
            // 添加目录条目（以 '/' 结尾）
            val dirEntry = TarArchiveEntry(file, entryBaseName + "/")
            tarOut.putArchiveEntry(dirEntry)
            tarOut.closeArchiveEntry()
            // 遍历子项
            file.listFiles()?.forEach { child ->
                val childEntryName = entryBaseName + "/" + child.name
                addFileToTar(tarOut, child, childEntryName)
            }
        } else {
            val entry = TarArchiveEntry(file, entryBaseName)
            entry.size = file.length()
            tarOut.putArchiveEntry(entry)
            Files.copy(file.toPath(), tarOut)
            tarOut.closeArchiveEntry()
        }
    }

    /**
     * 从 Glob 模式中找出最底层存在的目录
     */
    private fun findDeepestExistingDir(baseDir: File, pattern: String): File? {
        // 将 pattern 按 '/' 拆分，找到第一个包含通配符的位置
        val parts = pattern.split('/')
        val wildcardIndex = parts.indexOfFirst { it.contains('*') || it.contains('?') }
        val fixedParts = if (wildcardIndex == -1) parts else parts.take(wildcardIndex)

        // 从固定部分逐步缩短，直到找到存在的目录
        var current = baseDir
        for (part in fixedParts) {
            val next = current.resolve(part)
            if (next.isDirectory) {
                current = next
            } else {
                break
            }
        }
        return if (current != baseDir) current else null
    }

    /**
     * 列出指定目录下的内容，返回格式化字符串
     */
    private fun listDirContents(dir: File, maxDepth: Int): String {
        val sb = StringBuilder()
        val basePath = dir.toPath()
        if (!dir.isDirectory) return "${dir.absolutePath} (非目录)"

        Files.walk(basePath, maxDepth).filter { it != basePath }.forEach { path ->
            val relative = basePath.relativize(path).toString()
            val type = if (Files.isDirectory(path)) "DIR " else "FILE"
            sb.appendLine("  $type  $relative")
        }
        return sb.toString()
    }
}
