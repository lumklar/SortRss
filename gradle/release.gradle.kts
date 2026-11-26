import buildlogic.release.*
import buildlogic.flavors.*

//TODO 支持 macOS：dmg/pkg 只能由 macOS 的 jpackage 产出，需在 macos runner 上构建

val version = project.version.toString()
val releaseVersion = version.replace("-SNAPSHOT", "")

registerReleaseTasks(
    listOf(
        ReleaseConfig(
            target = "backend",
            moduleName = ":server:bootstrap",
            moduleTask = "bootJar",
            artifactRelativePath = "libs/bootstrap-$version.jar",
            envVarsCombinations = listOf(
                emptyList()
            ),
            group = "server",
            architectureIndependent = true,
        ),
        ReleaseConfig(
            target = "wasmjs",
            moduleName = ":app:webApp",
            moduleTask = "wasmJsBrowserDistribution",
            artifactRelativePath = "dist/wasmJs/productionExecutable",
            compression = CompressionMode.GZIP,
            envVarsCombinations = listOf(
                listOf(
                    DataFlavor.NETWORK
                )
            ),
            group = "web",
            architectureIndependent = true,
        ),
        ReleaseConfig(
            target = "js",
            moduleName = ":app:webApp",
            moduleTask = "jsBrowserDistribution",
            artifactRelativePath = "dist/js/productionExecutable",
            compression = CompressionMode.GZIP,
            envVarsCombinations = listOf(
                listOf(
                    DataFlavor.NETWORK
                )
            ),
            group = "web",
            architectureIndependent = true,
        ),
        // ========== Android ==========
        ReleaseConfig(
            target = "apk",
            moduleName = ":app:androidApp",
            moduleTask = "assembleRelease",
            // 签名后为 androidApp-release.apk；未配置签名时为 -unsigned.apk。
            // 两种都能匹配，assembleRelease 前的 cleanOldReleaseArtifacts 保证只会存在一个
            artifactRelativePath = "outputs/apk/release/androidApp-release*.apk",
            envVarsCombinations = listOf(
                listOf(
                    DataFlavor.NETWORK
                )
            ),
            group = "android",
            architectureIndependent = true,
        ),
        // ========== macOS ==========
        // jpackage 产物名可能带架构后缀，用通配符匹配；
        // 输出名由 renameTo 统一加 -x64 / -arm64 后缀（见 ReleaseExtensions）。
        // 未签名包：首次打开需右键 → 打开，或 xattr -cr 绕过 Gatekeeper。
        ReleaseConfig(
            target = "dmg",
            moduleName = ":app:desktopApp",
            moduleTask = "packageReleaseDmg",
            artifactRelativePath = "compose/binaries/main-release/dmg/*.dmg",
            envVarsCombinations = listOf(
                listOf(
                    DataFlavor.NETWORK
                )
            ),
            group = "mac"
        ),
        ReleaseConfig(
            target = "pkg",
            moduleName = ":app:desktopApp",
            moduleTask = "packageReleasePkg",
            artifactRelativePath = "compose/binaries/main-release/pkg/*.pkg",
            envVarsCombinations = listOf(
                listOf(
                    DataFlavor.NETWORK
                )
            ),
            group = "mac"
        ),
        // ========== Windows ==========
        ReleaseConfig(
            target = "exe",
            moduleName = ":app:desktopApp",
            moduleTask = "packageReleaseExe",
            artifactRelativePath = "compose/binaries/main-release/exe/SortRSS-$releaseVersion.exe",
            envVarsCombinations = listOf(
                listOf(
                    DataFlavor.NETWORK
                )
            ),
            group = "win"
        ),
        ReleaseConfig(
            target = "msi",
            moduleName = ":app:desktopApp",
            moduleTask = "packageReleaseMsi",
            artifactRelativePath = "compose/binaries/main-release/msi/SortRSS-$releaseVersion.msi",
            envVarsCombinations = listOf(
                listOf(
                    DataFlavor.NETWORK
                )
            ),
            group = "win"
        ),
        // ========== Linux ==========
        // jpackage 产物命名里带架构（amd64/arm64、x86_64/aarch64），
        // 用 * 通配避免在 x64 与 arm64 两个 runner 上维护两套路径。
        // 注意 deb/rpm/app-image 均带架构后缀，两个平台的产物不会互相覆盖。
        ReleaseConfig(
            target = "deb",
            moduleName = ":app:desktopApp",
            moduleTask = "packageReleaseDeb",
            // jpackage 实测产物：sortrss_0.0.1_amd64.deb（无 -1 release 段）
            artifactRelativePath = "compose/binaries/main-release/deb/*_${releaseVersion}_*.deb",
            envVarsCombinations = listOf(
                listOf(
                    DataFlavor.NETWORK
                )
            ),
            group = "linux"
        ),
        ReleaseConfig(
            target = "rpm",
            moduleName = ":app:desktopApp",
            moduleTask = "packageReleaseRpm",
            // <包名>-<版本>-<release>.<rpm 架构>.rpm
            artifactRelativePath = "compose/binaries/main-release/rpm/*-${releaseVersion}-*.*.rpm",
            envVarsCombinations = listOf(
                listOf(
                    DataFlavor.NETWORK
                )
            ),
            group = "linux"
        ),
        ReleaseConfig(
            // 实际是 jpackage app-image 目录的 tar.gz，不是单文件 AppImage，
            // 产物名如实写为 app-image，避免误导用户。
            target = "app-image",
            moduleName = ":app:desktopApp",
            moduleTask = "packageReleaseAppImage",
            // 注意：Compose 的 TargetFormat.AppImage 实际是 `jpackage --type app-image`，
            // 产物是免安装目录（含内置 JRE），不是 .AppImage 单文件，因此打包成 tar.gz。
            artifactRelativePath = "compose/binaries/main-release/app/*",
            compression = CompressionMode.GZIP,
            envVarsCombinations = listOf(
                listOf(
                    DataFlavor.NETWORK
                )
            ),
            group = "linux"
        )
    )
)