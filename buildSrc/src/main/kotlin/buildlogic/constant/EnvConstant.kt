package buildlogic.constant

/**
 * 任务级环境变量常量。
 *
 * 这里只放不对应 Gradle 属性的环境变量。与属性键一一对应的环境变量
 * （如 `site.url` -> `SITE_URL`）由 `EnvPropertyConverter` 推导，
 * 统一通过 `getConfigString` / `getEnvString` 读取，避免两处定义静默冲突。
 */
object EnvConstant {
    const val DOCKER_PLATFORMS = "DOCKER_PLATFORMS"
    const val DOCKER_IMAGE_VERSION = "DOCKER_IMAGE_VERSION"
}
