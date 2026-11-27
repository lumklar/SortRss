package buildlogic.release

/**
 * 发布产物的共享命名来源。
 *
 * 安装名（jpackage `packageName`）用首字母大写 [PACKAGE_NAME]，
 * 下载产物统一用小写前缀 [ARTIFACT_PREFIX]，两者语义不同，不要互相替换：
 * - 安装名是用户可见的应用/目录名；
 * - 产物名是 CI 与用户下载文件名（历史约定为 `sortrss-...`）。
 */
object ReleaseNames {
    const val PACKAGE_NAME = "SortRSS"
    const val ARTIFACT_PREFIX = "sortrss"

    /** 发布任务名前缀，如 `release-win`、`release-exe-network`。 */
    const val TASK_PREFIX = "release"
}
