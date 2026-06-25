package buildlogic.docker

import buildlogic.flavors.StringEnum

/**
 * 风味组合
 */
data class FlavorCombination(
    private val flavors: Set<StringEnum> // 使用 Set 可以天然去重对象，但我们需要自定义校验逻辑
) {
    // 如果希望用 vararg 可变参数构造，可以添加第二个构造函数
    constructor(vararg flavors: StringEnum) : this(flavors.toSet())

    init {
        // 1. 校验 Key 不重复（业务层面必须唯一）
        val keyList = flavors.map { it.key }
        val duplicateKeys = keyList.groupBy { it }.filter { it.value.size > 1 }.keys
        require(duplicateKeys.isEmpty()) {
            "Flavor keys must be unique. Duplicate keys found: $duplicateKeys"
        }

        // 2. 校验枚举类不重复（防止同一个 Enum 传入多个实例）
        val classList = flavors.map { it::class }
        val duplicateClasses = classList.groupBy { it }.filter { it.value.size > 1 }.keys
        require(duplicateClasses.isEmpty()) {
            "Flavor enum classes must be distinct. Duplicate classes found: $duplicateClasses"
        }

        // （可选）同时校验 Key 对应的枚举类是否唯一，防止两个不同枚举类写了同一个 Key
        // 但上面的校验已经包含了这一点，因为如果Key相同，第一个条件就会触发。
    }

    /**
     * 获取不可变列表
     */
    fun getFlavors(): List<StringEnum> = flavors.toList()

    /**
     * 将所有 Flavor 的 key=value 拼接成一个字符串，
     * 用于日志或描述性文本，例如 "data=mock, cache=memory"
     */
    fun toKeyValueString(): String {
        return flavors.joinToString(", ") { "${it.key}=${it.value}" }
    }

    // 便捷访问：通过具体类型获取值（若存在）
    fun <T : StringEnum> getFlavor(clazz: Class<T>): T? {
        return flavors.find { clazz.isInstance(it) } as? T
    }
}

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
    val flavors: List<FlavorCombination> = emptyList()
)