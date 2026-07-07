package buildlogic.docker

import buildlogic.flavors.StringEnum

data class DockerConfig(
    val targetName: String,
    //TODO 改为文件名而不是目录？
    val dockerfileDir: String,
    val namespace: String,
    val repository: String,
    val registryList: List<String>,
    val envVars: Map<String, String> = emptyMap(),
    val dependencies: List<Pair<String, String>> = emptyList(),
    val stringEnums: List<List<StringEnum>> = emptyList(),
    val platforms: List<String>,
    val versionLatest: Boolean = false,
    val latest: Boolean = false
)