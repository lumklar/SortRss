# 构建、校验与发布

## 1. 本机构建

Windows 用 `.\gradlew.bat`，其他环境用 `./gradlew`。

| 目的 | 命令 | 产物/说明 |
| --- | --- | --- |
| 看任务 | `gradlew tasks --all` | 任务名以各 `build.gradle.kts` 与 `gradle/*.gradle.kts` 为准 |
| 跑校验 | `gradlew check` | 连带 `checkModuleNames` / `checkDependencyVersions` / `checkPluginVersions` / `checkConfigKeys` / `checkVersionOverrides` |
| 依赖升级报告 | `gradlew dependencyUpdates` | 只查 release 版，输出 HTML |
| Web（wasmJs） | `gradlew :app:webApp:wasmJsBrowserDistribution` | `app/webApp/build/dist/wasmJs/productionExecutable` |
| 桌面端运行 | `gradlew :app:desktopApp:run` | `mainClass = io.github.lumklar.sortrss.app.desktop.MainKt` |
| Android debug APK | `gradlew :app:androidApp:assembleDebug` | `app/androidApp/build/outputs/apk/debug/` |
| 服务端 | `gradlew :server:bootstrap:bootRun` | 唯一可执行入口 |
| 文档站 | `gradlew :docs:mkdocsBuild` | 产物同步到 `docs/build/dist` |
| 首页+文档+demo 组装 | `gradlew :home:prepareDistributionWithMike` | `home/build/dist`（docs / demo / 静态站） |

- 构建输出统一落各模块 `build/`；**构建日志写 `ai_agents/logs/`**，不要写在仓库里。
- 非 CI 环境默认走阿里云镜像，`CI=true` 时走官方仓库（逻辑在根 `build.gradle.kts`）。
- 数据风味由 `flavor.data` 控制，默认 `mock`，可选 `network` / `local`（`gradle.properties` 或 `-P`/环境变量 `FLAVOR_DATA`）。

## 2. Android

- 入口 `:app:androidApp`。AGP 9.x 下共享模块用 `alias(libs.plugins.android.kmp.library)` + `kotlin { android {} }`，
  入口壳用 `com.android.application`，Kotlin 入口代码放 `src/main/kotlin`。
- 发布 APK：`gradlew release-apk-network`（`gradle/release.gradle.kts` 注册，NETWORK flavor），
  产物 `build/release/sortrss-apk-network-<version>.apk`。
- `:app:androidApp:cleanOldReleaseArtifacts` 挂在 `assembleRelease` 之前，删除 `build/release/sortrss-apk-*.apk`
  与模块内残留 release APK，因此可重复执行不会同名冲突。
- **签名**：仓库工作区内不存放密钥文件，也不出现具体密钥名称/路径。
  - 本地：根 `local.properties` 配 `android.keystore.file`（仓库外绝对路径）或 `android.keystore.base64`，
    以及 `android.keystore.password` / `android.key.alias` / `android.key.password`
  - CI：通过 `ANDROID_KEYSTORE_BASE64` 等环境变量注入，`prepareSigningKeystore` 解码到系统临时目录
  - 取值优先级见 `docs/dev/build-layout.md` §3.3
- CI 的 Android job 需 `android-actions/setup-android@v3` + `sdkmanager "platforms;android-37.0" "build-tools;36.0.0"`
  （Runner 不预装 API 37，包名是 `android-37.0`）。
- `:common:api` 的 `kapt`（therapi-scribe）只服务 JVM 侧 springdoc 生成 Swagger 元数据，**必须保留**；
  AGP 9 下 Android kapt 任务不兼容，已在该模块禁用 `kaptAndroid*` / `kaptGenerateStubsAndroid*`。

## 3. Docker

在根项目执行（任务由 `buildSrc` 的 `createDockerTask` 生成）：

| 任务 | 作用 |
| --- | --- |
| `buildDockerImageJvmWasmJs` | 构建单镜像（后端 jar + wasmJs 前端），注入 `VERSION`、`DIST_ROOT` |
| `buildFlavorDockerImageJvmWasmJsNetwork` | 带 `network` 风味的镜像构建 |
| `pushMultiArchFlavorDockerImageJvmWasmJsNetwork` | `docker buildx` 一次构建推送多平台镜像（原有入口） |
| `pushArchDockerImageJvmWasmJsNetwork<Platform>` | 单平台构建推送，标签带 `-linux-amd64` 等后缀；未选中的平台自动跳过 |
| `mergeArchDockerImageJvmWasmJsNetwork` | `docker buildx imagetools create`，把单平台镜像合成多架构清单；自动依赖全部 `pushArch*` |
| `buildAllDockerImages` / `buildAllFlavorDockerImages` / `pushAllMultiArchFlavorDockerImage` | 聚合入口（原有） |
| `pushAllArchFlavorDockerImage` / `mergeAllArchFlavorDockerImage` | 新流程聚合入口：先推全部单架构镜像，再合并清单 |

- 镜像名 `registry/namespace/repository:<version>-jvm-wasm-js[-network]`，默认 `ghcr.io/lumklar/sortrss`。
- 平台列表默认来自 `DockerConfig.platforms`，可用环境变量 `DOCKER_PLATFORMS`（逗号分隔）覆盖；
  标签可用 `DOCKER_IMAGE_VERSION`（逗号分隔，如 `dev,dev-<sha>`）覆盖。两者都在**任务执行期**解析：
  任务图只取决于 `DockerConfig`，配置缓存命中后改环境变量仍然生效。
- 项目版本与 `DOCKER_IMAGE_VERSION` 统一走 Docker tag 校验：只允许 `[a-z0-9][a-z0-9._-]{0,127}`，
  `dev`、`dev-abc123` 可用，`dev/foo`、`..` 等会在写镜像名前直接失败。
- 单架构任务按 `DockerConfig.platforms` 的全集注册；`DOCKER_PLATFORMS` 只选中其中一部分时，
  其余任务打印 `Skip` 后直接返回。若环境变量指定了配置里没有的平台，合并任务会报错提示补全声明。
- 并发安全：依赖任务的子 Gradle 调用按依赖集合、docker 命令按镜像标签分别加文件锁，
  锁文件在根项目 `build/docker-locks/`，不入库；不同平台/风味的镜像可以并行，相同标签串行。
- Dockerfile 见 `scripts/docker/jvm-with-frontend/`，构建上下文是仓库根。

## 4. 发布产物

`gradlew release-<target>` / `release-<group>` / `release-all`，产物统一落根 `build/release/`：

| target | 模块任务 | 说明 |
| --- | --- | --- |
| `backend` | `:server:bootstrap:bootJar` | 服务端 jar，架构无关 |
| `wasmjs` | `:app:webApp:wasmJsBrowserDistribution` | GZIP 压缩 |
| `js` | `:app:webApp:jsBrowserDistribution` | GZIP 压缩 |
| `apk` | `:app:androidApp:assembleRelease` | NETWORK flavor，需签名配置 |
| `exe` / `msi` | `:app:desktopApp:packageReleaseExe/Msi` | Windows |
| `dmg` / `pkg` | `:app:desktopApp:packageReleaseDmg/Pkg` | macOS（未签名），CI 用 `macos-15-intel`、`macos-15` 分别出 x64/arm64 |
| `deb` / `rpm` | `:app:desktopApp:packageReleaseDeb/Rpm` | Linux，由 jpackage 产出，文件名含包架构（`amd64`/`arm64`、`x86_64`/`aarch64`） |
| `app-image` | `:app:desktopApp:packageReleaseAppImage` | Linux 免安装目录（Compose 的 `TargetFormat.AppImage` 实为 `jpackage --type app-image`，不是 `.AppImage` 单文件），打 tar.gz；产物名沿用历史的 `appimage` |

命名规则：`sortrss-<artifactName>[-<arch>][-<flavor>]-<version>[.gz|.xz|...]`。
`artifactName` 默认等于 `target`，仅在两者需不同时显式声明（当前 `app-image` 任务产出 `appimage` 文件）；
任务名与产物名、分组均在配置期校验唯一性。安装名用 `SortRSS`，与产物名小写前缀分别来自 `ReleaseNames`。
分组聚合为 `release-server` / `release-web` / `release-android` / `release-win` / `release-mac` / `release-linux`。

桌面端产物只能在对应宿主系统上产出（jpackage 的平台限制）：Windows 出 `exe/msi`，macOS 出 `dmg/pkg`，Linux 出 `deb/rpm/app-image`。
在 Windows/macOS 上验证 Linux 打包，用容器最省事（需 JDK 21 与 `rpm`、`fakeroot`、`file`）：

```bash
docker run --rm -v "$PWD":/work -w /work ubuntu:24.04 bash -lc \
  "apt-get update && apt-get install -y openjdk-21-jdk rpm fakeroot file && ./gradlew release-linux"
```

## 5. 文档站与首页

- `:docs`：`mkdocsBuild`（`src/doc` → `build/dist`）、`mikeDeploy*`（mike 版本化，推到 `docs` 分支）。
  `src/doc/scripts/hooks.py` 从环境变量注入 `REPO_URL` / `SITE_URL` / `VERSION`。
- `:home`：Node/npm 构建，`prepareDistribution`（接 `:docs:moveDocs`）与
  `prepareDistributionWithMike`（接 `:docs:moveMikeDocs`）把首页、docs、demo 组装到 `home/build/dist`。

## 6. CI 工作流（`.github/workflows/`）

| 文件 | 触发 | 作用 |
| --- | --- | --- |
| `deploy-page.yml` | push `main` | `mikeDeployLatest` + `:home:prepareDistributionWithMike` → 推 `gh-pages` |
| `deploy-doc.yml` | push `v*` 分支 | `mikeDeployPush`，发布版本化文档到 `docs` 分支 |
| `push-dev-docker.yml` | push `dev` | `DOCKER_IMAGE_VERSION=dev,dev-<sha>` 推 dev 镜像 |
| `realease-docker.yml` | tag `v*` | 构建并推送发布镜像 |
| `release-binary.yml` | tag `v*` | 矩阵分平台执行 `release-<group>` 并上传 Release |
| `clean-up.yml` | 每日定时 | 清理 GHCR 旧 dev 标签与悬空镜像 |
| `cla.yml` / `update-cla-version.yml` | PR / `CLA.md` 变更 | CLA 签署校验与版本号维护 |
| `auto‑create‑labels.yml` | 手动 | 初始化仓库标签 |

注意：工作流文件名里存在全角连字符（`auto‑create‑labels.yml`），改名前先确认引用关系。
改动工作流时需在 PR 说明影响范围。
