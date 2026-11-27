import buildlogic.checks.registerVersionOverrideCheck
import org.gradle.api.artifacts.VersionCatalogsExtension
import java.util.Properties

/**
 * 统一管理临时版本覆盖：配置文件只声明「BOM 属性名 -> 版本目录别名」，
 * 版本号始终留在 gradle/libs.versions.toml；checkVersionOverrides 负责发现过期覆盖。
 */

val overridesFile = rootProject.file("gradle/version-overrides.properties")
val versionCatalogFile = rootProject.file("gradle/libs.versions.toml")
val catalog = extensions.getByType(VersionCatalogsExtension::class.java).named("libs")

val overrides = linkedMapOf<String, Pair<String, String>>()
if (overridesFile.isFile) {
    val properties = Properties().apply {
        overridesFile.bufferedReader(Charsets.UTF_8).use { load(it) }
    }
    properties.forEach { (rawKey, rawAlias) ->
        val bomProperty = rawKey.toString().trim()
        val alias = rawAlias.toString().trim()
        require(bomProperty.isNotBlank() && alias.isNotBlank()) {
            "version-overrides.properties 中存在空的 BOM 属性名或版本别名"
        }
        val version = catalog.findVersion(alias)
            .orElseThrow { error("version-overrides.properties 引用了不存在的版本别名 '$alias'，请在 gradle/libs.versions.toml 的 [versions] 中声明") }
            .requiredVersion
        overrides[bomProperty] = alias to version
        allprojects { extra[bomProperty] = version }
    }
}

val versionOverrideCheck = registerVersionOverrideCheck(
    overridesFile = overridesFile,
    versionCatalogFile = versionCatalogFile,
    overrides = overrides,
    springBootVersion = catalog.findVersion("spring-boot").orElseThrow { error("版本目录缺少 spring-boot 版本") }.requiredVersion
)
tasks.named("check") { dependsOn(versionOverrideCheck) }
