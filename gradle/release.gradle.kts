import buildlogic.release.*
import buildlogic.flavors.*

//FIXME 支持mac和linux，排查问题

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
        // ========== macOS ==========
//        ReleaseConfig(
//            target = "dmg",
//            moduleName = ":app:desktopApp",
//            moduleTask = "packageReleaseDmg",
//            artifactRelativePath = "compose/binaries/main-release/dmg/SortRSS-$releaseVersion.dmg",
//            envVarsCombinations = listOf(
//                listOf(
//                    DataFlavor.NETWORK
//                )
//            ),
//            group = "mac"
//        ),
//        ReleaseConfig(
//            target = "pkg",
//            moduleName = ":app:desktopApp",
//            moduleTask = "packageReleasePkg",
//            artifactRelativePath = "compose/binaries/main-release/pkg/SortRSS-$releaseVersion.pkg",
//            envVarsCombinations = listOf(
//                listOf(
//                    DataFlavor.NETWORK
//                )
//            ),
//            group = "mac"
//        ),
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
//        ReleaseConfig(
//            target = "deb",
//            moduleName = ":app:desktopApp",
//            moduleTask = "packageReleaseDeb",
//            artifactRelativePath = "compose/binaries/main-release/deb/sortrss_${releaseVersion}-1_amd64.deb",
//            envVarsCombinations = listOf(
//                listOf(
//                    DataFlavor.NETWORK
//                )
//            ),
//            group = "linux"
//        ),
//        ReleaseConfig(
//            target = "rpm",
//            moduleName = ":app:desktopApp",
//            moduleTask = "packageReleaseRpm",
//            artifactRelativePath = "compose/binaries/main-release/rpm/sortrss-${releaseVersion}-1.x86_64.rpm",
//            envVarsCombinations = listOf(
//                listOf(
//                    DataFlavor.NETWORK
//                )
//            ),
//            group = "linux"
//        ),
//        ReleaseConfig(
//            target = "appimage",
//            moduleName = ":app:desktopApp",
//            moduleTask = "packageReleaseAppImage",
//            artifactRelativePath = "compose/binaries/main-release/appimage/SortRSS-$releaseVersion.AppImage",
//            envVarsCombinations = listOf(
//                listOf(
//                    DataFlavor.NETWORK
//                )
//            ),
//            group = "linux"
//        )
    )
)