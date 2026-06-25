import org.gradle.declarative.dsl.schema.FqName.Empty.packageName
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

    //TODO 需要删除吗？
    compileOnly(libs.compose.uiToolingPreview)
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
            packageVersion = project.version.toString().replace("-SNAPSHOT", "")
        }
    }
}
