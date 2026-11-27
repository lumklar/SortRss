package buildlogic.release

import buildlogic.flavors.StringEnum

/**
 * 发布配置数据类。
 *
 * [target] 仅用于命名 Gradle 任务（`release-<target>[-<flavor>]`）和分组键；
 * [artifactName] 用于产物文件名（`sortrss-<artifactName>[-<arch>][-<flavor>]-<version>`）。
 * 两者默认一致，可用 [artifactName] 单独指定产物名（如 `app-image` 任务产出 `appimage` 文件）。
 *
 * @property target 任务名标识，也是 [artifactName] 的默认值
 * @property artifactName 产物名标识；为 null 时取 [target]
 * @property moduleName 目标模块名称（如 ":app"）
 * @property moduleTask 目标模块中需要执行的任务名（如 "build"）
 * @property artifactRelativePath 产物相对目标模块 build 目录的路径（如 "libs/app.jar"）
 * @property compression 压缩方式
 * @property envVarsCombinations 环境变量组合列表，每个组合为一个 List<StringEnum>
 * @property group 分组名称，用于创建聚合任务
 * @property architectureIndependent 该发布任务是否与 CPU 架构无关。
 */
data class ReleaseConfig(
    val target: String,
    val artifactName: String? = null,
    val moduleName: String,
    val moduleTask: String,
    val artifactRelativePath: String,
    val compression: CompressionMode = CompressionMode.NONE,
    val envVarsCombinations: List<List<StringEnum>>,
    val group: String,
    val architectureIndependent: Boolean = false
) {
    /** 产物名标识，默认与任务名一致。 */
    val effectiveArtifactName: String get() = artifactName ?: target

    init {
        require(TARGET_REGEX.matches(target)) {
            "target '$target' 非法：只允许小写字母、数字和 '-'，且不能以 '-' 开头或结尾"
        }
        artifactName?.let {
            require(TARGET_REGEX.matches(it)) {
                "artifactName '$it' 非法：只允许小写字母、数字和 '-'，且不能以 '-' 开头或结尾"
            }
        }
    }

    private companion object {
        val TARGET_REGEX = Regex("^[a-z0-9]+(-[a-z0-9]+)*$")
    }
}
