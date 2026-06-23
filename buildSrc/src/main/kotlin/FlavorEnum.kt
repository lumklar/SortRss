/**
 * 枚举值统一接口
 */
interface StringEnum {
    val value: String
}

/**
 * 数据风味枚举
 */
enum class DataFlavor(override val value: String) : StringEnum {
    MOCK("mock"),
    NETWORK("network"),
    LOCAL("local")
}