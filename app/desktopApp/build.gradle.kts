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
            // macOS 的 jpackage 要求 app-version 首位非 0，否则报
            // "The first number in an app-version cannot be zero or negative"，
            // 而项目版本是 0.0.1-SNAPSHOT。Compose DSL 没有独立 appVersion，
            // 它直接用 packageVersion 作为 jpackage 的 --app-version，
            // 因此仅 macOS 把首位 0 映射为 1；产物文件名由 release 的
            // renameTo 控制（用 project.version），不受影响。
            val rawVersion = project.version.toString().replace("-SNAPSHOT", "")
            packageVersion = if (System.getProperty("os.name").contains("Mac", ignoreCase = true)) {
                rawVersion.replace(Regex("^0\\."), "1.")
            } else {
                rawVersion
            }
            // deb/rpm/msi 的元数据：不设置时包内 Maintainer / License 会写成 Unknown
            vendor = "lumklar"
            description = "SortRSS - RSS aggregation, sorting and management"
            copyright = "Copyright 2026 lumklar"
        }
    }
}
