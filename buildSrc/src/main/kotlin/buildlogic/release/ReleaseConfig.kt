package buildlogic.release

import buildlogic.flavors.StringEnum

/**
 * 发布配置数据类。
 *
 * @property target 目标标识，用于命名任务和产物
 * @property moduleName 目标模块名称（如 ":app"）
 * @property moduleTask 目标模块中需要执行的任务名（如 "build"）
 * @property artifactRelativePath 产物相对目标模块 build 目录的路径（如 "libs/app.jar"）
 * @property shouldPackage 是否打包为 tar.gz（true 打包，false 仅复制）
 * @property envVarsCombinations 环境变量组合列表，每个组合为一个 List<StringEnum>
 * @property group 分组名称，用于创建聚合任务
 */
data class ReleaseConfig(
    val target: String,
    val moduleName: String,
    val moduleTask: String,
    val artifactRelativePath: String,
    val shouldPackage: Boolean = false,
    val envVarsCombinations: List<List<StringEnum>>,
    val group: String
)
