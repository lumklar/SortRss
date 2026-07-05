package buildlogic.constant

/**
 * TODO 环境变量是否要支持全小写+.的方式配置？ConfigUtils增加只从env获取配置？
 */
object EnvConstant {
    const val DOCKER_PLATFORMS = "DOCKER_PLATFORMS"
    const val DOCKER_IMAGE_VERSION = "DOCKER_IMAGE_VERSION"
}