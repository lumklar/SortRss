package buildlogic.docker


/**
 * 镜像标签计算结果
 */
data class ImageTags(
    val primaryBuildTag: String,          // 主构建标签，如 "lumklar/sortrss:1.0-jvm-wasmJs"
    val buildTags: List<String>,          // 所有本地构建标签（不含 Registry）
    val pushTags: List<String>            // 所有推送标签（每个 Registry 组合一份）
)

/**
 * 统一计算镜像标签（构建用 / 推送用）
 * @param namespace 命名空间（如 "lumklar"）
 * @param imageName 镜像名（如 "sortrss"）
 * @param version 版本号（如 "1.0"）
 * @param suffix 标签后缀（如 "jvm-wasmJs"）
 * @param flavorSuffix 风味后缀（如 "-network"），无风味时传入空字符串或 null
 * @param tagAsLatestVariant 是否生成 latest-<suffix> 标签
 * @param tagAsGlobalLatest 是否生成 latest 标签
 * @param registries 注册表列表，为空时 pushTags 也为空
 */
fun resolveImageTags(
    namespace: String,
    imageName: String,
    version: String,
    suffix: String,
//    flavorSuffix: String = "",
    tagAsLatestVariant: Boolean = true,
    tagAsGlobalLatest: Boolean = false,
    registries: List<String> = emptyList()
): ImageTags {
    //TODO 是否需要风味后缀，需要同步修改DockerTasks和FlavorTasks中的逻辑(包括多平台)
//    val fullSuffix = suffix + flavorSuffix
    val fullSuffix = suffix
    val baseWithoutVersion = "$namespace/$imageName"
    val versionTag = "$baseWithoutVersion:$version-$fullSuffix"

    val buildTags = mutableListOf(versionTag)
    if (tagAsLatestVariant) {
        buildTags.add("$baseWithoutVersion:latest-$fullSuffix")
    }
    if (tagAsGlobalLatest) {
        buildTags.add("$baseWithoutVersion:latest")
    }

    val pushTags = mutableListOf<String>()
    for (registry in registries) {
        val registryBase = "$registry/$namespace/$imageName"
        pushTags.add("$registryBase:$version-$fullSuffix")
        if (tagAsLatestVariant) {
            pushTags.add("$registryBase:latest-$fullSuffix")
        }
        if (tagAsGlobalLatest) {
            pushTags.add("$registryBase:latest")
        }
    }

    return ImageTags(
        primaryBuildTag = versionTag,
        buildTags = buildTags.distinct(),
        pushTags = pushTags.distinct()
    )
}

/**
 * 生成 Docker 镜像名称前缀（不含 Registry）
 * @param namespace  镜像命名空间（如组织或用户名）
 * @param imageName  镜像名称
 * @param version    项目版本号
 * @return 格式为 "namespace/imageName:version-" 的字符串
 */
fun buildImageNamePrefix(namespace: String, imageName: String, version: String): String {
    return "$namespace/$imageName:$version-"
}

/**
 * 从 Docker 镜像名称前缀中解析出 namespace、imageName 和 version
 * @param prefix 格式为 "namespace/imageName:version-" 的字符串
 * @return Triple(namespace, imageName, version)
 * @throws IllegalArgumentException 如果格式不符合预期
 */
fun parseImageNamePrefix(prefix: String): Triple<String, String, String> {
    // 1. 按第一个 '/' 分割，得到 namespace 和剩余部分
    val slashIndex = prefix.indexOf('/')
    require(slashIndex != -1) { "Invalid prefix: missing '/'" }
    val namespace = prefix.substring(0, slashIndex)
    val restAfterSlash = prefix.substring(slashIndex + 1)

    // 2. 按第一个 ':' 分割剩余部分，得到 imageName 和 version（带末尾 '-'）
    val colonIndex = restAfterSlash.indexOf(':')
    require(colonIndex != -1) { "Invalid prefix: missing ':'" }
    val imageName = restAfterSlash.substring(0, colonIndex)
    val versionWithDash = restAfterSlash.substring(colonIndex + 1)

    // 3. 去掉末尾的 '-'，得到真正的 version
    require(versionWithDash.endsWith('-')) { "Invalid prefix: version should end with '-'" }
    val version = versionWithDash.dropLast(1)

    return Triple(namespace, imageName, version)
}
