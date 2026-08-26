package buildlogic.docs

data class MikeDeployConfig(
    val workingDir: String,                // 工作目录路径，例如 "src/doc"
    val branch: String,                    // Git 分支，例如 "docs"
    val deployPrefix: String,              // 部署前缀，例如 "docs/"
    val versions: List<String>,            // 要部署的版本列表，例如 listOf("latest", "v1.0")
    val defaultVersion: String,            // 默认版本，必须在 versions 中
    val requirementsFile: String? = null,   // 可选：自定义 requirements.txt 路径，默认为 workingDir/requirements.txt
    val unpackDir: String? = null,        // 解压目录，默认 "build/mike"
    val archiveName: String? = null,       // 归档文件名（不含 .tar.gz），默认从 deployPrefix 提取
)
