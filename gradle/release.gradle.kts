import buildlogic.release.*
import buildlogic.flavors.*

//TODO 增加github/workflows/release.yml

val version = project.version.toString()
val releaseVersion = version.replace("-SNAPSHOT", "")

registerReleaseTasks(
    listOf(
        ReleaseConfig(
            target = "backend",
            moduleName = ":server",
            moduleTask = "bootJar",
            artifactRelativePath = "libs/server-$version.jar",
            shouldPackage = false,
            envVarsCombinations = listOf(
                emptyList()
            ),
            group = "server"
        ),
        ReleaseConfig(
            target = "wasmjs",
            moduleName = ":app:webApp",
            moduleTask = "wasmJsBrowserDistribution",
            artifactRelativePath = "dist/wasmJs/productionExecutable",
            shouldPackage = true,
            envVarsCombinations = listOf(
                listOf(
                    DataFlavor.NETWORK
                )
            ),
            group = "web"
        ),
        ReleaseConfig(
            target = "js",
            moduleName = ":app:webApp",
            moduleTask = "jsBrowserDistribution",
            artifactRelativePath = "dist/js/productionExecutable",
            shouldPackage = true,
            envVarsCombinations = listOf(
                listOf(
                    DataFlavor.NETWORK
                )
            ),
            group = "web"
        ),
        // ========== macOS ==========
        ReleaseConfig(
            target = "dmg",
            moduleName = ":app:desktopApp",
            moduleTask = "packageReleaseDmg",
            artifactRelativePath = "build/compose/binaries/main-release/dmg/SortRSS-$releaseVersion.dmg",
            shouldPackage = false,
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
            artifactRelativePath = "build/compose/binaries/main-release/pkg/SortRSS-$releaseVersion.pkg",
            shouldPackage = false,
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
            artifactRelativePath = "build/compose/binaries/main-release/exe/SortRSS-$releaseVersion.exe",
            shouldPackage = false,
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
            artifactRelativePath = "build/compose/binaries/main-release/msi/SortRSS-$releaseVersion.msi",
            shouldPackage = false,
            envVarsCombinations = listOf(
                listOf(
                    DataFlavor.NETWORK
                )
            ),
            group = "win"
        ),
        // ========== Linux ==========
        ReleaseConfig(
            target = "deb",
            moduleName = ":app:desktopApp",
            moduleTask = "packageReleaseDeb",
            artifactRelativePath = "build/compose/binaries/main-release/deb/SortRSS-$releaseVersion.deb",
            shouldPackage = false,
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
            artifactRelativePath = "build/compose/binaries/main-release/rpm/SortRSS-$releaseVersion.rpm",
            shouldPackage = false,
            envVarsCombinations = listOf(
                listOf(
                    DataFlavor.NETWORK
                )
            ),
            group = "linux"
        ),
        ReleaseConfig(
            target = "appimage",
            moduleName = ":app:desktopApp",
            moduleTask = "packageReleaseAppImage",
            artifactRelativePath = "build/compose/binaries/main-release/appimage/SortRSS-$releaseVersion.AppImage",
            shouldPackage = false,
            envVarsCombinations = listOf(
                listOf(
                    DataFlavor.NETWORK
                )
            ),
            group = "linux"
        )
    )
)