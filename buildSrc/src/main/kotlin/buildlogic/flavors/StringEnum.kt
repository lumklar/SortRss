package buildlogic.flavors

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
