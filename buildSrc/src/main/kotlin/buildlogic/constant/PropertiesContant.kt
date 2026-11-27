package buildlogic.constant

/**
 * Gradle 属性键的唯一声明处。
 *
 * 环境变量名由 [buildlogic.utils.EnvPropertyConverter] 从这里的属性键推导；
 * `checkConfigKeys` 会检查与 EnvConstant 是否重复或格式非法。
 */
object PropertiesContant {
    const val FLAVOR_PREFIX = "flavor."
    const val FLAVOR_DATA = FLAVOR_PREFIX + "data"

    const val DOCKER_PREFIX = "docker."
    const val DOCKER_REGISTRY = DOCKER_PREFIX + "registry"
    const val DOCKER_NAMESPACE = DOCKER_PREFIX + "namespace"
    const val DOCKER_REPOSITORY = DOCKER_PREFIX + "repository"
    const val DOCKER_TAG_SUFFIX = DOCKER_PREFIX + "tag.suffix"

    const val SITE_URL = "site.url"
    const val REPO_URL = "repo.url"
}