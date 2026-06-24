package buildlogic.docker

import buildlogic.flavors.DataFlavor

// ========== 数据类 ==========
data class FlavorCombination(
    val data: DataFlavor,
)

data class DockerTaskConfig(
    val dockerFileRelativePath: String,
    val suffix: String,
    val buildArgs: Map<String, String> = emptyMap(),
    val tagAsGlobalLatest: Boolean = false,
    val dependencies: List<String> = emptyList(),
    val flavors: FlavorCombination? = null
)
