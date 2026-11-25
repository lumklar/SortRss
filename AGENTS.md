# AGENTS.md

本文件约束 AI 代理（以及人类协作者）在本仓库中的工作方式。**动手前先读这里**：
先确认目标文件属于哪个目录、职责是什么，再决定放哪里。任何不属于项目产物的文件，
一律放进 `ai_agents/`。

---

## 1. 项目概览

SortRss 是一个智能 RSS 聚合 / 排序 / 管理工具，采用 **Kotlin Multiplatform 单仓多模块**结构，
客户端（Compose Multiplatform）与服务端（Spring Boot）共享 `common` 模块。

- 语言 / 版本：Kotlin 2.4.0、JVM Toolchain 21
- 客户端：Compose Multiplatform 1.11.1、Ktor Client 3.5.2、kotlinx.serialization
- 服务端：Spring Boot 4.1.1、kotlin-spring、JPA + SQLite（sqlite-jpa）
- 目标平台：`jvm` / `js(browser)` / `wasmJs(browser)` / Android（AGP 9.1.0，入口为 `:app:androidApp`）
- 构建：Gradle Wrapper 9.7.1 + `refreshVersions`（`settings.gradle.kts`）+ `ben-manes.versions`（根工程与 `buildSrc` 各自声明）

### 1.1 版本目录：**两套**，各自独立

| 文件 | 管什么 | 在哪个脚本中表现为 `libs` |
| --- | --- | --- |
| `gradle/libs.versions.toml` | 主工程全部模块（`:app:*`、`:client:*`、`:common:*`、`:server:*`、`:home`、`:docs`）及根 `build.gradle.kts` 的插件版本 | 根项目与各子模块 |
| `buildSrc/gradle/libs.versions.toml` | `buildSrc/` 自身构建逻辑的依赖与插件（`kotlin-dsl`、`commons-compress`、`xz`、`zstd-jni` 等） | **仅** `buildSrc/build.gradle.kts` |

- **`buildSrc` 读不到根版本目录**：`buildSrc` 是独立构建，其中的 `libs` 指向自己目录下的
  `buildSrc/gradle/libs.versions.toml`。`buildSrc/settings.gradle.kts` 里"从
  `../gradle/libs.versions.toml` 导入 `libs`"的代码当前是**注释状态**；确有需要再启用，
  启用后以根目录为准，避免两处维护。
- **重复条目必须同步升级**：`ben-manes-versions`（当前 0.62.0）与 `commons-compress`
  （当前 1.28.0）在两套目录中各有一份。只改一边会导致"依赖检查插件版本"与"构建逻辑实
  际使用的版本"漂移，改版本时务必两处一起改。
- **改版本先认门**：模块依赖 → 根 `gradle/libs.versions.toml`；构建逻辑（`buildSrc/src` 用到的库、
  `kotlin-dsl` 插件）→ `buildSrc/gradle/libs.versions.toml`。
- 模块脚本一律用 `alias(libs.xxx)` / `libs.xxx` 引用，**禁止硬编码版本号**。

---

## 2. 目录与模块职责

### 顶层目录

| 目录 | 职责 | 边界约束 |
| --- | --- | --- |
| `app/` | 各平台**入口壳**：启动、打包、平台适配 | 只放与平台启动相关的代码，业务逻辑不得写在这里 |
| `client/` | KMP 客户端：契约、数据实现、UI | 只依赖 `common/*`，不得反向被 `common/*` 依赖 |
| `common/` | 跨端共享层：领域、业务、契约、基础设施 | 客户端与服务端共用，禁止引入 `client/*` 或 `server/*` |
| `server/` | Spring Boot 服务端（分层架构） | 只有 `server:bootstrap` 是可执行入口 |
| `buildSrc/` | 构建逻辑：约定插件与自定义任务（`src/main/kotlin/buildlogic/{checks,constant,docker,...}`） | 不放业务代码；依赖与插件版本由**自带的** `buildSrc/gradle/libs.versions.toml` 管理（见 §1.1） |
| `gradle/` | **主工程**版本目录 `libs.versions.toml`、wrapper、构建脚本片段（`common` / `docker` / `release` / `check`） | 根 `build.gradle.kts` 通过 `apply(from=...)` 引入；不供 `buildSrc` 使用 |
| `docs/` | MkDocs 文档站源码（`docs/src/doc`）与文档构建脚本 | 构建产物落 `build/`，不放源码以外内容 |
| `home/` | 首页静态站点与前端构建（Node/npm） | `node_modules/` 已忽略 |
| `scripts/` | **仓库级、可复用**的发布/构建脚本（如 `scripts/docker/`） | 一次性脚本不放这里，放 `ai_agents/scripts/` |
| `data/` | 本地运行数据库文件（`rss.db`，已忽略） | 不得提交数据库文件 |
| `.github/` | CI/CD 工作流、Issue/PR 模板 | 改动工作流需说明影响 |
| `kotlin-js-store/` | Kotlin/JS & Wasm 依赖锁文件 | 由构建自动生成，勿手改 |
| `ai_agents/` | **非项目文件唯一允许存放地** | 已加入 `.gitignore`，不参与构建与提交 |

### 已纳入构建的模块（见 `settings.gradle.kts`）

```
:app:webApp              wasmJs 浏览器端入口
:app:desktopApp          JVM 桌面端入口
:app:androidApp          Android 入口（assembleDebug 可产出 APK）

:client:composer         客户端组装 / 依赖注入
:client:contract:data    客户端数据契约（接口、模型）
:client:contract:all     契约聚合出口
:client:impl-data:network  契约实现：Ktor 网络
:client:impl-data:mock     契约实现：内存 mock
:client:impl-data:local    契约实现：本地持久化
:client:ui:theme         Compose 主题、字体、资源
:client:ui:components    公共 UI 组件（当前仅有构建脚本，待填充）
:client:ui:navigation    页面导航
:client:ui:feature:feed  业务功能：信息流
:client:ui:app           UI 应用装配 / 根组合

:common:domain           领域模型（纯 Kotlin，无框架依赖）
:common:shared           通用工具与基础能力
:common:api              对外 API 契约 / DTO
:common:business         业务逻辑（依赖 domain、shared）
:common:foundation       基础设施封装（日志、配置等）

:server:application      应用层 / 用例（依赖 common:domain、common:business）
:server:infrastructure   技术实现（持久化等）
:server:adaptor          适配层（HTTP 入口等）
:server:bootstrap        启动与装配，唯一可执行入口

:home                    首页站点
:docs                    文档站点
```

---

## 3. 非项目文件归属（强制）

**判定标准：这个文件是不是"仓库产物"（源码、配置、构建脚本、正式文档、CI 定义）？
不是 → 必须放 `ai_agents/`。**

包括但不限于：计划、方案、调研笔记、任务拆解、改动说明、验证记录、临时脚本
（`.bat` / `.ps1` / `.sh` / `.py`）、运行日志、构建日志、截图、测试数据、导出的
中间产物、解包出来的源码副本。

### `ai_agents/` 子目录约定

| 目录 | 放什么 | 生命周期 |
| --- | --- | --- |
| `ai_agents/plan/` | 计划、方案、设计草稿、任务拆解、调研结论 | 任务结束后保留或自行归档 |
| `ai_agents/impl/` | 实施记录、改动说明、验证步骤、回归清单 | 可随任务结束清理 |
| `ai_agents/scripts/` | 一次性 / 临时脚本（排查、批处理、数据修复） | 用完即删，或按 §3.2 转正 |
| `ai_agents/logs/` | 运行日志、构建日志、调试输出 | 定期清理，禁止提交 |
| `ai_agents/output/` | 生成的报告、图表、导出文件等交付中间物 | 交付后清理 |
| `ai_agents/tmp/` | 中间文件、缓存、解压产物、实验用副本 | 随时可删 |

### 3.1 命名规范

```
YYYYMMDD-<简短英文或拼音>-<描述>.md     例：20261125-构建失败排查.md
YYYYMMDD-<主题>.log                     例：20261125-gradle-build.log
YYYYMMDD-<用途>.bat|sh|py               例：20261125-清缓存.bat
```

### 3.2 转正规则

临时文件被证明**长期有价值**时，才允许移出 `ai_agents/`：

- 可复用脚本 → `scripts/`（如属构建流程 → `gradle/*.gradle.kts` 或 `buildSrc/`）
- 正式文档 → `docs/src/doc/docs/`
- 项目级规范 → 更新本文件

移入后必须同步更新本文件对应目录说明；**未经转正的文件不得进入版本库**。

### 3.3 禁止事项

- ❌ 在仓库根目录或模块目录下新建 `.bat` / `.ps1` / `.sh` / `.log` / `*.md` 草稿、截图、
  `*.db`、解压目录、临时副本
- ❌ 把日志、临时脚本、本地数据写进 `src/` 或模块根目录
- ❌ 把需要长期保留的文件放进 `build/`（构建清理会删除）
- ❌ 修改 `.gitignore` 放行 `ai_agents/`（该目录仅供本地使用）

---

## 4. 依赖与分层规则

依赖方向（单向，禁止逆流）：

```
app:*            ─┐
                  ├─→ client:*（contract / ui / composer）
client:*         ─┘        │
                           ↓
server:bootstrap ─→ server:{adaptor, application, infrastructure}
                           ↓
                    common:{business, api, foundation, shared}
                           ↓
                    common:domain
```

- `common:domain` 不得依赖任何其他模块，也不得引入框架依赖
- `common:*` 不得依赖 `client:*` / `server:*` / `app:*`
- UI 层只依赖 `client:contract:*`，**不得直接依赖 `impl-data:*`**；实现由 `client:composer` 装配
- 新增模块必须同时完成：① `settings.gradle.kts` 中 `include`；② 必要时补**根** `gradle/libs.versions.toml`；
  ③ 在本文件 §2 登记职责
- 版本目录分两套：模块用根 `gradle/libs.versions.toml`，`buildSrc` 用自带的
  `buildSrc/gradle/libs.versions.toml`；两边重复条目需同步升级（见 §1.1）

---

## 5. 构建与验证

- Windows 下优先用 `.\gradlew.bat <任务>`，其他环境用 `./gradlew <任务>`
- 具体任务名以各模块 `build.gradle.kts` 与 `gradle/*.gradle.kts` 定义为准；服务端入口在 `:server:bootstrap`
- Android 端入口为 `:app:androidApp`：`./gradlew.bat :app:androidApp:assembleDebug` 产出 APK（输出在 `app/androidApp/build/outputs/apk/debug/`）。AGP 9.x 下共享模块统一用 `alias(libs.plugins.android.kmp.library)` + `kotlin { android {} }`，入口壳用 `com.android.application`，Kotlin 入口代码放 `src/main/kotlin`
- 构建输出统一落 `build/`；**构建日志写到 `ai_agents/logs/`**，不要写在仓库目录
- 本地仓库镜像：非 CI 环境默认走阿里云镜像（见 `build.gradle.kts`），CI 环境走官方仓库

---

## 6. 提交前检查清单

1. `git status --short` 结果中只应出现源码 / 配置 / 构建脚本 / 正式文档等仓库产物
2. 每个新增文件都能回答"它属于哪个模块、承担什么职责"；答不上来 → 移入 `ai_agents/`
3. 暂存区里没有 `build/`、日志、临时脚本、本地数据库、截图、`__pycache__/`
4. 若改动了 `ben-manes-versions` / `commons-compress` 等两边都有的版本，确认两套版本目录已同步（§1.1）
5. 若改动了目录职责、模块依赖或构建约定，同步更新本文件
