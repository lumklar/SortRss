package buildlogic.docker

import buildlogic.flavors.DataFlavor

/**
 * 风味组合
 */
data class FlavorCombination(
    val data: DataFlavor,
)

/**
 * 描述一个 Docker 构建配置
 * @param dockerFileRelativePath Dockerfile 相对路径（相对于 deploy/docker）
 * @param suffix 镜像标签后缀（如 "jvm-wasmJs"）
 * @param buildArgs 构建参数
 * @param tagAsGlobalLatest 是否同时打上 `latest` 标签
 * @param dependencies 前置任务列表
 * @param flavors 可空的风味组合，用于生成风味包装任务
 */
data class DockerTaskConfig(
    val dockerFileRelativePath: String,
    val suffix: String,
    val buildArgs: Map<String, String> = emptyMap(),
    val tagAsGlobalLatest: Boolean = false,
    val dependencies: List<String> = emptyList(),
    val flavors: FlavorCombination? = null
)