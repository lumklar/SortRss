package buildlogic.checks

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

abstract class CheckPluginVersionsTask : DefaultTask() {

    @Input
    lateinit var buildFileContents: Map<String, String>
        private set

    fun setBuildFileContents(contents: Map<String, String>) {
        this.buildFileContents = contents
    }

    init {
        group = "verification"
        description = "检查所有 build.gradle(.kts) 中的插件版本是否通过 libs.versions.toml 管理"
    }

    @TaskAction
    fun check() {
        val errors = mutableListOf<String>()

        buildFileContents.forEach { (filePath, content) ->
            val pluginsBlock = extractPluginsBlock(content) ?: return@forEach

            // 匹配插件声明：id("plugin.id") version "x.y.z" 或 id 'plugin.id' version 'x.y.z'
            val pluginRegex = Regex("""id\s*\(?\s*["']([^"']+)["']\s*\)?\s+version\s+["']([^"']+)["']""")
            val matches = pluginRegex.findAll(pluginsBlock)

            matches.forEach { match ->
                val pluginId = match.groupValues[1]
                val version = match.groupValues[2]

                // 如果版本字符串不包含 "libs." 或变量引用，则视为硬编码
                if (!version.contains("libs.") && !version.contains("\$")) {
                    // 计算行号
                    val lineNum = content.substring(0, match.range.first).count { it == '\n' } + 1
                    errors.add(
                        "$filePath (行 $lineNum): " +
                                "插件 '$pluginId' 版本 '$version' 应替换为 libs.versions.xxx 或 alias 引用"
                    )
                }
            }
        }

        if (errors.isNotEmpty()) {
            val count = errors.size
            logger.error("❌ Found $count plugin version declaration(s) not managed via version catalog (libs).")
            errors.forEach { logger.error("  $it") }
            throw GradleException("Plugin version catalog compliance check failed: $count violations found.")
        } else {
            logger.lifecycle("✅ All plugin versions are managed via libs.versions.toml.")
        }
    }

    // ---------- 提取 plugins {} 块 ----------
    private fun extractPluginsBlock(content: String): String? {
        // 支持 plugins { ... } 和 plugins { ... } 可能嵌套（但通常不嵌套）
        val regex = Regex("""plugins\s*\{""")
        val matchResult = regex.find(content) ?: return null
        var startIdx = matchResult.range.first

        var braceDepth = 0
        var inString = false
        var inComment = false
        var escape = false
        var i = startIdx

        while (i < content.length) {
            val c = content[i]

            if (escape) {
                escape = false
                i++
                continue
            }
            if (c == '\\') {
                escape = true
                i++
                continue
            }

            if (c == '"' || c == '\'') {
                if (!inComment) inString = !inString
                i++
                continue
            }

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
                if (c == '{') {
                    braceDepth++
                } else if (c == '}') {
                    braceDepth--
                    if (braceDepth == 0) {
                        return content.substring(startIdx, i + 1)
                    }
                }
            }
            i++
        }
        return null
    }
}