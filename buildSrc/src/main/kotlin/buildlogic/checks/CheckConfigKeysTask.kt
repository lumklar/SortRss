package buildlogic.checks

import buildlogic.utils.EnvPropertyConverter
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction

/**
 * 检查 Gradle 属性键与环境变量常量是否冲突。
 *
 * `PropertiesContant` 是属性键的唯一来源，环境变量名由 `EnvPropertyConverter` 推导；
 * `EnvConstant` 只允许放不对应任何 Gradle 属性的任务级环境变量（如 DOCKER_PLATFORMS）。
 */
abstract class CheckConfigKeysTask : DefaultTask() {

    @get:InputFile
    abstract val propertiesFile: RegularFileProperty

    @get:InputFile
    abstract val envFile: RegularFileProperty

    init {
        group = "verification"
        description = "检查属性键与环境变量常量是否重复或格式非法"
    }

    @TaskAction
    fun check() {
        val properties = parseConstants(propertiesFile.get().asFile)
        val envs = parseConstants(envFile.get().asFile)
        val errors = mutableListOf<String>()

        val propertyPattern = Regex("^[a-z][a-z0-9]*(\\.[a-z][a-z0-9]*)*$")
        val prefixPattern = Regex("^[a-z][a-z0-9]*\\.$")
        val envPattern = Regex("^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$")

        properties.forEach { (name, value) ->
            val valid = if (name.endsWith("_PREFIX")) prefixPattern.matches(value) else propertyPattern.matches(value)
            if (!valid) {
                errors.add("PropertiesContant.$name = '$value' 不是合法的小写点分隔属性键或前缀")
            }
        }
        envs.forEach { (name, value) ->
            if (!envPattern.matches(value)) {
                errors.add("EnvConstant.$name = '$value' 不是合法的大写下划线环境变量名")
            }
        }

        listOf("PropertiesContant" to properties, "EnvConstant" to envs).forEach { (objectName, constants) ->
            constants.groupBy { it.second }
                .filter { it.value.size > 1 }
                .forEach { (value, names) ->
                    errors.add(
                        "$objectName 中键 '$value' 重复定义：${names.joinToString(", ") { it.first }}"
                    )
                }
        }

        val derivedEnvNames = properties
            .filterNot { it.first.endsWith("_PREFIX") }
            .map { EnvPropertyConverter.propertyToEnv(it.second) }
            .toSet()
        envs.forEach { (name, value) ->
            if (value in derivedEnvNames) {
                errors.add(
                    "EnvConstant.$name = '$value' 与 PropertiesContant 中的属性键推导结果冲突；" +
                        "请直接用属性键调用 getConfigString，不要在 EnvConstant 重复声明"
                )
            }
        }

        if (errors.isNotEmpty()) {
            logger.error("❌ Found ${errors.size} configuration key problem(s).")
            errors.forEach { logger.error("  $it") }
            throw GradleException("Configuration key check failed: ${errors.size} problem(s) found.")
        }
        logger.lifecycle("✅ Configuration property keys and environment constants do not conflict.")
    }

    private fun parseConstants(file: java.io.File): List<Pair<String, String>> {
        if (!file.isFile) return emptyList()
        val regex = Regex("""const\s+val\s+(\w+)\s*=\s*"([^"]+)"""")
        return regex.findAll(file.readText()).map { match ->
            match.groupValues[1] to match.groupValues[2]
        }.toList()
    }
}
