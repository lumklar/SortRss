package buildlogic.checks

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

abstract class CheckDependencyVersionsTask : DefaultTask() {

    @Input
    lateinit var buildFileContents: Map<String, String>
        private set

    @Input
    var dependencyConfigs: Set<String> = setOf(
        "implementation", "api", "compileOnly", "runtimeOnly",
        "testImplementation", "testApi", "testCompileOnly", "testRuntimeOnly",
        "androidTestImplementation", "androidTestApi",
        "kapt", "ksp", "annotationProcessor",
        "debugImplementation", "releaseImplementation"
    )
        private set

    fun setBuildFileContents(contents: Map<String, String>) {
        this.buildFileContents = contents
    }

    fun setDependencyConfigs(configs: Set<String>) {
        this.dependencyConfigs = configs
    }

    init {
        group = "verification"
        description = "检查所有依赖是否通过 libs.versions.toml 管理"
    }

    @TaskAction
    fun check() {
        val errors = mutableListOf<String>()

        buildFileContents.forEach { (filePath, content) ->
            val depsBlock = extractBlock(content, "dependencies") ?: return@forEach

            val configRegex = Regex("""\b(${dependencyConfigs.joinToString("|")})\b""")
            val matches = configRegex.findAll(depsBlock)

            matches.forEach { match ->
                val paramStart = match.range.last + 1
                val param = extractArgument(depsBlock, paramStart)

                if (param.isNotEmpty() && !isValidCatalogUsage(param)) {
                    if (param.contains(":") || param.contains("\$")) {
                        val lineNum = content.substring(0, match.range.first).count { it == '\n' } + 1
                        errors.add(
                            "$filePath (行 $lineNum): " +
                                    "${match.value} \"$param\" 应替换为 libs.xxx"
                        )
                    }
                }
            }
        }

        if (errors.isNotEmpty()) {
            val count = errors.size
            logger.error("❌ Found $count dependency declaration(s) not managed via version catalog (libs).")
            errors.forEach { logger.error("  $it") }
            throw GradleException("Dependency version catalog compliance check failed: $count violations found.")
        } else {
            logger.lifecycle("✅ All dependencies are managed via libs.versions.toml.")
        }
    }

    // ---------- 以下为工具函数（完全不变） ----------
    private fun extractBlock(content: String, keyword: String): String? {
        val regex = Regex("""$keyword\s*\{""")
        val matchResult = regex.find(content) ?: return null
        var startIdx = matchResult.range.first
        var braceDepth = 0
        var inString = false
        var inComment = false
        var escape = false
        var i = startIdx
        while (i < content.length) {
            val c = content[i]
            if (escape) { escape = false; i++; continue }
            if (c == '\\') { escape = true; i++; continue }
            if (c == '"' || c == '\'') { if (!inComment) inString = !inString; i++; continue }
            if (!inString) {
                if (c == '/' && i + 1 < content.length && content[i + 1] == '/') {
                    while (i < content.length && content[i] != '\n') i++
                    continue
                }
                if (c == '/' && i + 1 < content.length && content[i + 1] == '*') {
                    inComment = true
                    i += 2
                    continue
                }
                if (inComment && c == '*' && i + 1 < content.length && content[i + 1] == '/') {
                    inComment = false
                    i += 2
                    continue
                }
            }
            if (!inString && !inComment) {
                if (c == '{') braceDepth++
                else if (c == '}') {
                    braceDepth--
                    if (braceDepth == 0) return content.substring(startIdx, i + 1)
                }
            }
            i++
        }
        return null
    }

    private fun extractArgument(text: String, startPos: Int): String {
        var i = startPos
        while (i < text.length && text[i].isWhitespace()) i++
        if (i >= text.length) return ""
        return when (val c = text[i]) {
            '"', '\'' -> {
                i++
                val start = i
                while (i < text.length && text[i] != c) {
                    if (text[i] == '\\') i++
                    i++
                }
                text.substring(start, i)
            }
            '(' -> {
                var depth = 1
                i++
                val start = i
                while (i < text.length && depth > 0) {
                    when (text[i]) {
                        '(' -> depth++
                        ')' -> depth--
                        '"', '\'' -> {
                            val q = text[i]
                            i++
                            while (i < text.length && text[i] != q) {
                                if (text[i] == '\\') i++
                                i++
                            }
                        }
                    }
                    if (depth > 0) i++
                }
                text.substring(start, i).trim()
            }
            else -> {
                val start = i
                while (i < text.length && !text[i].isWhitespace() && text[i] != ')' && text[i] != ',') i++
                text.substring(start, i)
            }
        }
    }

    private fun isValidCatalogUsage(param: String): Boolean {
        val trimmed = param.trim()
        return trimmed.contains("libs.") ||
                trimmed.contains("project(") ||
                trimmed.contains("files(") ||
                trimmed.contains("rootProject.") ||
                trimmed.startsWith("gradleApi()") ||
                trimmed.startsWith("localGroovy()")
    }
}
