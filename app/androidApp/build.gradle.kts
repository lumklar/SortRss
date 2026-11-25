import buildlogic.utils.getConfigString
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.io.File
import java.util.Base64

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
}

// Android 发布签名：仓库内不存放、也不引用任何具体密钥文件
// - 本地：local.properties 中 android.keystore.file 指向仓库外的密钥路径，或 android.keystore.base64
// - CI：环境变量 ANDROID_KEYSTORE_BASE64 等注入，构建时解码到系统临时目录
val keystoreBase64 = getConfigString("android.keystore.base64")
val keystorePath = getConfigString("android.keystore.file")
val keystorePassword = getConfigString("android.keystore.password")
val signingKeyAlias = getConfigString("android.key.alias")
val signingKeyPassword = getConfigString("android.key.password")

// base64 模式的解码目标：系统临时目录（仓库外），名称与具体产品/密钥无关
val decodedKeystore = File(System.getProperty("java.io.tmpdir"), "android-signing/signing-material.keystore")

val prepareSigningKeystore = tasks.register("prepareSigningKeystore") {
    group = "release"
    description = "从 ANDROID_KEYSTORE_BASE64 解码签名密钥到系统临时目录"
    val base64 = keystoreBase64
    val target = decodedKeystore
    onlyIf { !base64.isNullOrBlank() }
    outputs.upToDateWhen { false }
    doLast {
        target.parentFile.mkdirs()
        target.writeBytes(Base64.getMimeDecoder().decode(requireNotNull(base64)))
    }
}

val signingStoreFile = when {
    !keystoreBase64.isNullOrBlank() -> decodedKeystore
    !keystorePath.isNullOrBlank() -> rootProject.file(keystorePath)
    else -> null
}
val releaseSigningReady = signingStoreFile != null &&
        listOf(keystorePassword, signingKeyAlias, signingKeyPassword).all { !it.isNullOrBlank() }

// base64 模式下，密钥文件在构建期才生成，相关任务必须等待解码完成
if (!keystoreBase64.isNullOrBlank()) {
    tasks.matching {
        it.name.startsWith("package") ||
                it.name.startsWith("validateSigning") ||
                it.name.startsWith("writeReleaseSigning")
    }.configureEach {
        dependsOn(prepareSigningKeystore)
    }
}

// 发布前清理：发布框架用 Files.copy 且不覆盖同名文件，重跑发布会失败；
// 同时清掉上一次构建残留的 signed/unsigned APK，避免产物匹配到多个文件
val cleanOldReleaseArtifacts = tasks.register<Delete>("cleanOldReleaseArtifacts") {
    group = "release"
    description = "删除 build/release 下旧的 Android APK 及模块内过期的 release APK"
    delete(rootProject.fileTree("build/release").matching { include("sortrss-apk-*.apk") })
    delete(fileTree("build/outputs/apk/release").matching { include("*.apk") })
}
tasks.matching { it.name == "assembleRelease" }.configureEach {
    dependsOn(cleanOldReleaseArtifacts)
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_11
    }
}
dependencies {
    implementation(project(":client:composer"))

    implementation(libs.androidx.activity.compose)

    implementation(libs.compose.uiToolingPreview)
    debugImplementation(libs.compose.uiTooling)
}

android {
    namespace = "com.github.lumklar.sortrss"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.github.lumklar.sortrss"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    signingConfigs {
        if (releaseSigningReady) {
            create("release") {
                storeFile = signingStoreFile
                storePassword = keystorePassword
                keyAlias = signingKeyAlias
                keyPassword = signingKeyPassword
            }
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            if (releaseSigningReady) {
                signingConfig = signingConfigs.getByName("release")
            } else {
                logger.warn("Android 发布签名未配置（缺少密钥 base64、密钥路径或密码），将产出 unsigned APK")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
//    buildFeatures {
//        compose = true
//    }
}
