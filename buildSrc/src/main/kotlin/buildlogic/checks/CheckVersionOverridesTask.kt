package buildlogic.checks

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.TaskAction
import javax.xml.XMLConstants
import javax.xml.parsers.DocumentBuilderFactory

/**
 * 检查临时版本覆盖是否仍有必要。
 *
 * 覆盖声明放在 `gradle/version-overrides.properties`，值指向根版本目录中的 version 别名；
 * 若 Spring Boot BOM 自带版本已经不低于覆盖版本，说明漏洞修复已被上游吸收，必须删除
 * 覆盖声明与版本目录条目，避免老版本被长期钉住。
 */
abstract class CheckVersionOverridesTask : DefaultTask() {

    @get:InputFile
    abstract val overridesFile: RegularFileProperty

    @get:InputFile
    abstract val versionCatalogFile: RegularFileProperty

    @get:Input
    abstract val overrideVersions: MapProperty<String, String>

    @get:Input
    abstract val overrideAliases: MapProperty<String, String>

    @get:Input
    abstract val springBootVersion: Property<String>

    @get:InputFiles
    abstract val bomPom: ConfigurableFileCollection

    init {
        group = "verification"
        description = "检查临时版本覆盖是否仍必要，BOM 追平后强制删除"
    }

    @TaskAction
    fun check() {
        val versions = overrideVersions.get()
        val aliases = overrideAliases.get()
        if (versions.isEmpty()) {
            logger.lifecycle("✅ No temporary version overrides declared.")
            return
        }

        val factory = DocumentBuilderFactory.newInstance().apply {
            isNamespaceAware = false
            setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true)
        }
        val bomDocument = factory.newDocumentBuilder().parse(bomPom.singleFile)
        val errors = mutableListOf<String>()

        versions.toSortedMap().forEach { (bomProperty, overrideVersion) ->
            val alias = aliases[bomProperty] ?: "<unknown>"
            val nodes = bomDocument.getElementsByTagName(bomProperty)
            if (nodes.length == 0) {
                errors.add(
                    "$bomProperty（别名 $alias）：Spring Boot ${springBootVersion.get()} 的 BOM 未声明该属性，无法判断覆盖是否过期"
                )
                return@forEach
            }
            val bomVersion = nodes.item(0).textContent.trim()
            if (compareVersion(overrideVersion, bomVersion) <= 0) {
                errors.add(
                    "$bomProperty（别名 $alias）：覆盖版本 $overrideVersion 已过期（BOM 为 $bomVersion），" +
                        "请删除 gradle/version-overrides.properties 中的声明和 gradle/libs.versions.toml 中的别名"
                )
            }
        }

        if (errors.isNotEmpty()) {
            logger.error("❌ Found ${errors.size} stale version override(s).")
            errors.forEach { logger.error("  $it") }
            throw GradleException("Version override check failed: ${errors.size} stale override(s) found.")
        }
        logger.lifecycle("✅ All ${versions.size} temporary version override(s) are still required.")
    }
}

/**
 * 按数字段比较版本号，非数字段按字符串比较；用于判断 BOM 是否已追平覆盖版本。
 */
private fun compareVersion(left: String, right: String): Int {
    fun normalize(value: String) = value.trim().lowercase().removePrefix("v")
    val leftParts = normalize(left).split(Regex("[.-]"))
    val rightParts = normalize(right).split(Regex("[.-]"))
    for (index in 0 until maxOf(leftParts.size, rightParts.size)) {
        val a = leftParts.getOrNull(index) ?: "0"
        val b = rightParts.getOrNull(index) ?: "0"
        val result = when {
            a.toIntOrNull() != null && b.toIntOrNull() != null -> a.toInt().compareTo(b.toInt())
            else -> a.compareTo(b)
        }
        if (result != 0) return result
    }
    return 0
}
