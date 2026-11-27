import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.compose.multiplatform)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

dependencies {
    implementation(project(":client:composer"))

    implementation(compose.desktop.currentOs)
    implementation(libs.kotlinx.coroutinesSwing)

    implementation(libs.compose.uiToolingPreview)
}
compose.desktop {
    application {
        mainClass = "io.github.lumklar.sortrss.app.desktop.MainKt"

        nativeDistributions {
            targetFormats(
                TargetFormat.Dmg,  // macOS
                TargetFormat.Pkg,  // macOS
                TargetFormat.Exe,  // Windows
                TargetFormat.Msi,  // Windows
                TargetFormat.Deb,  // Linux (Debian/Ubuntu)
                TargetFormat.Rpm,  // Linux (Red Hat/Fedora/CentOS)
                TargetFormat.AppImage, // Linux
            )
            //TODO 统一变量名
            packageName = "SortRSS"
            // jpackage 不接受 SNAPSHOT 后缀，统一去掉后作为安装包版本。
            // 项目版本已升到 1.0.0，首位非 0，macOS 无需再做 0→1 映射；
            // 产物文件名由 release 的 renameTo 控制（用 project.version），不受影响。
            packageVersion = project.version.toString().replace("-SNAPSHOT", "")
            // deb/rpm/msi 的元数据：不设置时包内 Maintainer / License 会写成 Unknown
            vendor = "lumklar"
            description = "SortRSS - RSS aggregation, sorting and management"
            copyright = "Copyright 2026 lumklar"
        }
    }
}
