package buildlogic.utils

import org.gradle.api.Project

internal fun Project.gradlewPath(): String {
    val gradlew = if (System.getProperty("os.name").startsWith("Windows")) "gradlew.bat" else "gradlew"
    return rootProject.projectDir.resolve(gradlew).absolutePath
}