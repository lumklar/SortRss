package buildlogic.docker

import buildlogic.flavors.StringEnum

data class DockerConfig(
    val target: String,
    val dockerfileDir: String,
    val namespace: String,
    val version: String,
    val registryList: List<String>,
    val envVars: Map<String, String> = emptyMap(),
    val dependencies: List<Pair<String, String>> = emptyList(),
    val stringEnums: List<StringEnum> = emptyList(),
    val platforms: List<String>,
    val versionLatest: Boolean,
    val latest: Boolean
)