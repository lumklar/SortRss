package buildlogic.constant

/**
 * TODO 检查与EnvConstant中是否冲突
 */
object PropertiesContant {
    const val FLAVOR_PREFIX = "flavor."
    const val FLAVOR_DATA = FLAVOR_PREFIX + "data"

    const val DOCKER_PREFIX = "docker."
    const val DOCKER_REGISTRY = DOCKER_PREFIX + "registry"
    const val DOCKER_NAMESPACE = DOCKER_PREFIX + "namespace"
    const val DOCKER_REPOSITORY = DOCKER_PREFIX + "repository"
    const val DOCKER_TAG_SUFFIX = DOCKER_PREFIX + "tag.suffix"
}