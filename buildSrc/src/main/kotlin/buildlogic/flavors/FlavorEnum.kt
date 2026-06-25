package buildlogic.flavors

import buildlogic.constant.PropertiesContant
import buildlogic.utils.EnvPropertyConverter

/**
 * 枚举值统一接口
 */
interface StringEnum {
    val value: String
    val key: String
    val envKey: String
        get() = EnvPropertyConverter.propertyToEnv(key)
}

/**
 * 数据风味枚举
 */
enum class DataFlavor(override val value: String) : StringEnum {
    MOCK("mock"),
    NETWORK("network"),
    LOCAL("local");

    companion object {
        const val KEY = PropertiesContant.FLAVOR_DATA   // 静态常量
        @JvmField
        val ENV_KEY = EnvPropertyConverter.propertyToEnv(KEY)   // 静态常量
    }

    override val key: String get() = KEY
}