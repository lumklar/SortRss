# 构建目录说明：scripts / gradle / buildSrc

> 三者分工：`buildSrc/` 提供**可复用的构建能力**（Task、扩展函数、常量、工具），
> `gradle/*.gradle.kts` 负责**装配与配置**（读配置、调扩展函数、注册任务），
> `scripts/` 存放**被构建引用的外部脚本/镜像定义**。
> 一句话：能复用 → `buildSrc`；只做声明与拼装 → `gradle`；给 Docker 等外部工具用 → `scripts`。

---

## 1. `scripts/`

仓库级、可复用、会被构建或 CI 直接引用的脚本。**一次性排查脚本不放这里**，放 `ai_agents/scripts/`。

| 路径 | 用途 | 谁在用 |
| --- | --- | --- |
| `scripts/docker/jvm-with-frontend/Dockerfile` | 单镜像“后端 + 前端”部署：`eclipse-temurin:25-jre` 基础镜像，`COPY` 后端 `bootstrap-${VERSION}.jar` 与 wasmJs 产物到 `./static/`，暴露 80 端口，默认 `SQLITE_DB_PATH=/data/rss.db`，启动参数把静态资源指向 `file:./static/` | `gradle/docker.gradle.kts` 中 `DockerConfig(targetName = "jvm-wasmJs", dockerfileDir = "...")` |

约定：

- Docker 构建上下文固定为**仓库根**（`buildDockerCommand` 里 `add(projectDir.absolutePath)`），
  所以 Dockerfile 内的 `COPY` 路径都相对仓库根书写。
- `ARG VERSION` / `ARG DIST_ROOT` 由 Gradle 通过 `--build-arg` 注入，改产物的相对路径时两边一起改。
- 新增 Dockerfile 时按 `<用途>/Dockerfile` 建子目录，并在 `gradle/docker.gradle.kts` 里加一条 `DockerConfig`。

---

## 2. `gradle/`

主工程的版本目录、Wrapper 与构建脚本片段。根 `build.gradle.kts` 末尾用 `apply(from = "...")` 依次引入。

| 文件 | 用途 |
| --- | --- |
| `libs.versions.toml` | **主工程**版本目录：全部子模块与根构建脚本使用的依赖、插件版本（`libs.*` / `alias(libs.plugins.*)`） |
| `common.gradle.kts` | 子项目公共配置入口，目前仅 `subprojects { }` 空壳，用于将来统一约定 |
| `check.gradle.kts` | `registerAllChecks()`：注册四个 `verification` 任务并挂到根 `check` |
| `version-overrides.gradle.kts` | 读 `version-overrides.properties`，把临时覆盖写入 `extra`，并注册 `checkVersionOverrides` |
| `version-overrides.properties` | 临时安全覆盖声明（`BOM 属性名=版本目录别名`），当前为空 |
| `docker.gradle.kts` | 读取 `docker.registry / namespace / repository`，用一条 `DockerConfig` 声明 `jvm-wasmJs` 镜像的构建与多架构推送任务 |
| `release.gradle.kts` | `registerReleaseTasks(...)`：声明 `backend / wasmjs / js / apk / dmg / pkg / exe / msi / deb / rpm / app-image` 全部发布产物（macOS/Linux 已启用） |
| `wrapper/gradle-wrapper.jar` | Wrapper 引导 jar，勿手改 |
| `wrapper/gradle-wrapper.properties` | 锁定 Gradle 9.7.1、`distributionSha256Sum`，并开启 configuration cache |

补充：

- 仓库镜像策略在根 `build.gradle.kts`：非 CI 走阿里云镜像 + 官方兜底，`CI=true` 时只走官方仓库。
- 根 `build.gradle.kts` 还把 Android / Kotlin / Compose 等插件以 `apply false` 预置到 classpath，
  避免子项目重复加载导致 `NoClassDefFoundError`；新增插件时同步加到这段 `plugins {}`。

**改版本先认门**：模块依赖 → `gradle/libs.versions.toml`；`buildSrc` 自身依赖 → `buildSrc/gradle/libs.versions.toml`。

---

## 3. `buildSrc/`

独立构建（有自己的 settings 与版本目录），产物是可直接在构建脚本中 `import buildlogic.*` 使用的 Kotlin 类。

### 3.1 构建配置

| 文件 | 用途 |
| --- | --- |
| `build.gradle.kts` | `kotlin-dsl` 插件；依赖 `gradleApi()`、`commons-compress`、`xz`、`zstd-jni`；启用 `ben-manes.versions` 并把 `dependencyUpdates` 输出为 HTML |
| `settings.gradle.kts` | 目前只有一段**注释**：启用后可从 `../gradle/libs.versions.toml` 导入 `libs`。默认不启用，故 `buildSrc` 的 `libs` 指向自己目录下的版本目录 |
| `gradle/libs.versions.toml` | **buildSrc 专用**版本目录：`commons-compress`、`xz`、`zstd-jni`（含备用 brotli / lz4 / snappy）、`ben-manes-versions` |

### 3.2 `src/main/kotlin/buildlogic/` 各目录

| 目录 | 用途 | 主要文件 |
| --- | --- | --- |
| `checks/` | 四个 `verification` 任务：模块名唯一、依赖/插件版本走版本目录、配置键无冲突；`registerAllChecks()` 统一注册并挂到 `check`。`CheckVersionOverridesTask` 由 version-overrides 脚本单独注册 | `CheckExtensions.kt`（任务名常量 + `register*Check()`）、`CheckModuleNamesTask.kt`、`CheckDependencyVersionsTask.kt`、`CheckPluginVersionsTask.kt`、`CheckConfigKeysTask.kt`、`CheckVersionOverridesTask.kt` |
| `constant/` | 配置键常量，避免字符串散落 | `PropertiesContant.kt`（`flavor.data`、`docker.*`、`site.url`、`repo.url`，唯一属性键来源）、`EnvConstant.kt`（仅 `DOCKER_PLATFORMS`、`DOCKER_IMAGE_VERSION` 等任务级环境变量） |
| `docker/` | 镜像构建与多架构发布 | `DockerConfig.kt`（声明式配置）、`DockerExtensions.kt`（`createDockerTask`，装配 build / flavor build / push / pushArch / mergeArch 及五个聚合任务）、`DockerBuildTask.kt` + `DockerBuildExtensions.kt`（`docker build` 单镜像）、`DockerPublishMultiArchTask.kt`（buildx 一次构建多平台）、`DockerArchBuildTask.kt` / `DockerArchMergeTask.kt`（逐平台推送、`imagetools create` 合并）、`DockerPushExtensions.kt`（三类发布任务注册与公共输入装配）、`DockerImageTask.kt`（任务公共输入、执行期解析标签/平台）、`DockerTaskSupport.kt`（依赖子进程、docker 命令、文件锁、buildx 命令拼装）、`DockerUtils.kt`（拼 `docker build` 命令、镜像名与 tag，统一校验项目版本和环境变量版本） |
| `docs/` | MkDocs 站点构建与 mike 版本化部署 | `MikeDeployConfig.kt`（工作目录、分支、版本列表）、`MkDockerExtensions.kt`（`createMikeDeployTask`，产出 `install / deploy / deployPush / deployLatest / unpack` 任务）、`MkDocsDeployTask.kt`（调用 mike 的任务本体） |
| `flavors/` | “数据风味”枚举与配置解析 | `StringEnum.kt`（枚举统一接口，含 envKey 推导）、`FlavorEnum.kt`（`DataFlavor`：`mock` / `network` / `local`）、`FlavorUtils.kt`（`matchEnum`、`getFlavorEnum`） |
| `release/` | 产物发布：跑模块任务 → 匹配产物 → 拷贝/压缩到 `build/release` | `ReleaseConfig.kt`（`target` 任务名 / `artifactName` 产物名解耦，配置期校验唯一）、`ReleaseNames.kt`（安装名 SortRSS、产物前缀 sortrss 的共享来源）、`CompressionMode.kt`（NONE/GZIP/BZIP2/XZ/LZMA/ZSTANDARD/DEFLATE，默认参数已固化）、`ReleaseExtensions.kt`（`registerReleaseTasks`，生成 `release-<target>[-<env>]`、`release-<group>`、`release-all`）、`ReleasePublishTask.kt`（跨进程文件锁、打包压缩、重命名） |
| `toolchain/` | 调用外部工具的基础任务 | `GitTask.kt`（`GitArchiveUnpackTask`：从指定分支导出 tar.gz 并解压，供 mike 取历史版本）、`PythonTask.kt`（`PipInstallTask`：按 requirements.txt 装依赖） |
| `utils/` | 通用工具 | `ConfigUtils.kt`（`getConfigString` 五级优先级、`getEnvString` 只从 env 读）、`EnvPropertyConverter.kt`（`DATABASE_URL` ↔ `database.url`）、`GradleUtils.kt`（`gradlewPath()` 跨平台）、`NameUtils.kt`（kebab / lowerCamel 转换、版本号校验） |

### 3.3 关键约定

- **配置优先级**（`getConfigString`）：
  `-P` 命令行 > 系统属性 > 环境变量 > `gradle.properties` > 根 `local.properties`。
  环境变量同时接受 `DATABASE_URL`、`database.url` 和调用时传入的原始键，规范大写形式优先；
  `getEnvString` 只查环境变量，不回退到其他来源。敏感信息只进 `local.properties`（不入库）或 CI 环境变量。
- **任务命名**由 `getTaskName(prefix, targetName, enums)` 统一生成（lowerCamelCase），
  例：`buildDockerImageJvmWasmJs`、`buildFlavorDockerImageJvmWasmJsNetwork`、
  `pushMultiArchFlavorDockerImageJvmWasmJsNetwork`；镜像 tag 用 kebab-case：
  `<version>-jvm-wasm-js[-network]`。
- **发布任务命名**：`release-<target>[-<flavor>]`，聚合任务 `release-<group>`、`release-all`；
  产物名为 `sortrss-<artifactName>[-<arch>][-<flavor>]-<version>`，默认 `artifactName=target`，
  架构相关的目标会带 `-<arch>` 后缀，产物统一落根 `build/release/`。
  同模块任务既有 `mustRunAfter`（进程内排序），`ReleasePublishTask` 又按模块 build 目录加跨进程文件锁；
  jpackage 不同架构产物名的差异由 `artifactRelativePath` 通配符吸收。
- `buildSrc` 里的 Task 需满足 **configuration cache**：配置阶段捕获所有输入，执行阶段不访问 `Project`。

### 3.4 新增构建逻辑该放哪

| 需求 | 放哪 |
| --- | --- |
| 新的可复用 Task / 扩展函数 / 常量 | `buildSrc/src/main/kotlin/buildlogic/<领域>/` |
| 只在本仓库装配的配置（读属性、调扩展函数） | `gradle/<主题>.gradle.kts`，并在根 `build.gradle.kts` 里 `apply(from=...)` |
| 供 Docker 等外部工具读取的文件 | `scripts/<主题>/` |
| 一次性排查/批处理脚本 | `ai_agents/scripts/`（用完即删） |
| 新增 `buildSrc` 依赖 | `buildSrc/gradle/libs.versions.toml` + `buildSrc/build.gradle.kts` |
