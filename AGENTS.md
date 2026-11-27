# AGENTS.md

本文件约束 AI 代理与人类协作者在本仓库的工作方式。**动手前读完本文件（约 3 分钟）**：
先确认目标文件属于哪个目录，再决定放哪里。细节不在此堆叠，按下表跳 `docs/dev/`。

## 0. 三条硬约束（任何改动都不得破）

1. **非项目文件不入库**：计划、草稿、日志、临时脚本、截图、导出物一律进 `ai_agents/`（已 gitignore）。
2. **依赖单向**：`app → client → common ← server`；`common:domain` 不依赖任何模块与框架。
3. **版本号只写在版本目录**：模块依赖 → 根 `gradle/libs.versions.toml`；`buildSrc` → `buildSrc/gradle/libs.versions.toml`；构建脚本禁止硬编码版本。

## 1. 项目

SortRss：智能 RSS 聚合 / 排序 / 管理工具。Kotlin Multiplatform 单仓多模块，
Compose Multiplatform 客户端 + Spring Boot 服务端，共享 `common`。
Kotlin 2.4.0 / JDK 21 / Gradle 9.7.1；目标平台 `jvm`、`js`、`wasmJs`、Android。

## 2. 目录地图

| 目录 | 职责 | 边界 |
| --- | --- | --- |
| `app/` | 各平台入口壳：启动、打包、平台适配 | 不写业务逻辑 |
| `client/` | KMP 客户端：契约、数据实现、UI | 只依赖 `common/*` |
| `common/` | 跨端共享：领域、业务、契约、基础设施 | 不依赖 `client/*`、`server/*` |
| `server/` | Spring Boot 服务端（分层） | 只有 `server:bootstrap` 可执行 |
| `buildSrc/` | 构建逻辑：Task、扩展函数、常量、工具 | 不放业务代码，见 §3.3 |
| `gradle/` | 版本目录、Wrapper、构建脚本片段 | 由根 `build.gradle.kts` `apply(from=...)` 引入 |
| `scripts/` | 仓库级、可复用脚本（构建/CI 引用） | 一次性脚本 → `ai_agents/scripts/` |
| `docs/` | `src/doc` 对外站点；`dev/` 开发文档 | 见 §4 |
| `home/` | 首页静态站与前端构建，聚合 docs/demo | `node_modules/` 已忽略 |
| `data/` | 本地 `rss.db` | 不得提交 |
| `.github/` | CI 工作流、Issue/PR 模板 | 改动需说明影响 |
| `kotlin-js-store/` | JS/Wasm 依赖锁 | 自动生成，勿手改 |
| `ai_agents/` | 非项目文件唯一存放地 | 不入库，见 §4 |

模块清单与依赖方向详见 [docs/dev/architecture.md](docs/dev/architecture.md)。

## 3. 构建相关目录：文件用途

### 3.1 `scripts/`

| 路径 | 用途 |
| --- | --- |
| `docker/jvm-with-frontend/Dockerfile` | 单镜像部署：后端 `bootJar` + wasmJs 前端（`COPY` 到 `./static/`），端口 80，`SQLITE_DB_PATH=/data/rss.db`；上下文为仓库根，由 `gradle/docker.gradle.kts` 引用 |

### 3.2 `gradle/`

| 文件 | 用途 |
| --- | --- |
| `libs.versions.toml` | 主工程版本目录：全部模块依赖 + 根插件版本 |
| `common.gradle.kts` | 子项目公共配置（`subprojects {}`，当前空壳） |
| `check.gradle.kts` | `registerAllChecks()`：四个校验任务并挂到根 `check`；另由 `version-overrides.gradle.kts` 注册 `checkVersionOverrides` |
| `version-overrides.gradle.kts` | 读取 `version-overrides.properties` 的临时安全覆盖并写入 `extra`，注册过期检查 |
| `version-overrides.properties` | 临时覆盖声明：`BOM 属性名=版本目录别名`，当前为空 |
| `docker.gradle.kts` | 读 `docker.*` 配置，注册 `jvm-wasmJs` 镜像构建与多架构推送任务 |
| `release.gradle.kts` | `registerReleaseTasks(...)`：各平台产物 `release-*` / `release-all` |
| `wrapper/` | Gradle Wrapper 9.7.1（jar + properties），勿手改 |

### 3.3 `buildSrc/`

独立构建，自带版本目录；包根 `buildlogic`。

| 目录 | 用途 |
| --- | --- |
| `checks/` | 四个 `verification` 任务：模块重名、依赖版本、插件版本、配置键冲突（入口 `registerAllChecks`）；另有 `CheckVersionOverridesTask` 由版本覆盖脚本注册 |
| `constant/` | 配置键常量：`PropertiesContant`（Gradle 属性）、`EnvConstant`（环境变量） |
| `docker/` | 镜像构建/推送：`DockerConfig`、`DockerExtensions`（装配）、`DockerBuildTask`、`DockerPublishMultiArchTask`（buildx）、`DockerUtils`（命令与 tag） |
| `docs/` | MkDocs/mike 部署：`MikeDeployConfig`、`MkDockerExtensions`、`MikeDeployTask` |
| `flavors/` | `StringEnum`、`DataFlavor`（mock/network/local）、`FlavorUtils` 配置解析 |
| `release/` | 产物发布：`ReleaseConfig`、`CompressionMode`、`ReleaseExtensions`、`ReleasePublishTask` |
| `toolchain/` | 外部工具任务：`GitArchiveUnpackTask`、`PipInstallTask` |
| `utils/` | `ConfigUtils.getConfigString`（`-P` > 系统属性 > 环境变量 > `gradle.properties` > `local.properties`）、`EnvPropertyConverter`、`GradleUtils`、`NameUtils` |

逐文件说明、任务命名规则、新增构建逻辑放哪：见 [docs/dev/build-layout.md](docs/dev/build-layout.md)。

## 4. 文档归属

| 位置 | 放什么 |
| --- | --- |
| `docs/src/doc/docs/` | **仅对外使用文档**（安装、使用、功能介绍）；MkDocs 站点唯一来源，新页需加 `nav` |
| `docs/dev/` | 仓库开发文档：架构、构建、脚本、发布、开发约束 |
| `ai_agents/` | 计划、实施记录、日志、临时脚本、导出物（不入库） |
| 本文件 | 只放“每次必读”的硬约束与索引 |

读者是用软件的人 → `docs/src`；读者是改仓库的人或 AI → `docs/dev`。

## 5. 常用命令

```bash
gradlew check                                  # 含五个校验任务
gradlew :app:webApp:wasmJsBrowserDistribution
gradlew :app:androidApp:assembleDebug
gradlew :server:bootstrap:bootRun
gradlew release-apk-network                    # 产物 build/release/
```

Windows 用 `.\gradlew.bat`。日志写 `ai_agents/logs/`。详见 [docs/dev/build-and-release.md](docs/dev/build-and-release.md)。

## 6. 详细文档索引

| 文档 | 内容 |
| --- | --- |
| [docs/README.md](docs/README.md) | 文档目录划分与索引 |
| [docs/dev/architecture.md](docs/dev/architecture.md) | 目录职责、模块清单、分层规则 |
| [docs/dev/build-layout.md](docs/dev/build-layout.md) | `scripts/` / `gradle/` / `buildSrc/` 逐文件用途 |
| [docs/dev/build-and-release.md](docs/dev/build-and-release.md) | 构建命令、Android/签名、Docker、发布、CI |
| [docs/dev/versioning.md](docs/dev/versioning.md) | 两套版本目录与同步规则 |
| [docs/dev/ai-workflow.md](docs/dev/ai-workflow.md) | 完整工作约束、命名、转正、检查清单 |

## 7. 提交前检查

1. `git status --short` 只出现仓库产物（无 `build/`、日志、临时脚本、`.db`、截图）
2. 每个新增文件能说清“属于哪个模块、什么职责”，否则移入 `ai_agents/`
3. 改了两边重复版本（`ben-manes-versions`、`commons-compress`）→ 已两处同步
4. 改了目录职责 / 依赖 / 构建约定 → 同步更新本文件与 `docs/dev/` 对应页
