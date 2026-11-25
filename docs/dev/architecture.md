# 架构与目录职责

> 面向修改本仓库的人。新增文件前先在这里确认“属于哪个目录、承担什么职责”。

## 1. 项目概览

SortRss 是智能 RSS 聚合 / 排序 / 管理工具，Kotlin Multiplatform 单仓多模块：
Compose Multiplatform 客户端 + Spring Boot 服务端，共享 `common` 模块。

| 项 | 值 |
| --- | --- |
| 语言 / 工具链 | Kotlin 2.4.0、JVM Toolchain 21 |
| 客户端 | Compose Multiplatform 1.11.1、Ktor Client 3.5.2、kotlinx.serialization |
| 服务端 | Spring Boot 4.1.1、kotlin-spring、JPA + SQLite（sqlite-jpa） |
| 目标平台 | `jvm` / `js(browser)` / `wasmJs(browser)` / Android（AGP 9.1.0，入口 `:app:androidApp`） |
| 构建 | Gradle Wrapper 9.7.1 + `refreshVersions` + `ben-manes.versions` |

## 2. 顶层目录

| 目录 | 职责 | 边界约束 |
| --- | --- | --- |
| `app/` | 各平台**入口壳**：启动、打包、平台适配 | 只放平台启动相关代码，业务逻辑不得写在这里 |
| `client/` | KMP 客户端：契约、数据实现、UI | 只依赖 `common/*`，不得被 `common/*` 反向依赖 |
| `common/` | 跨端共享层：领域、业务、契约、基础设施 | 客户端与服务端共用，禁止引入 `client/*`、`server/*` |
| `server/` | Spring Boot 服务端（分层架构） | 只有 `server:bootstrap` 是可执行入口 |
| `buildSrc/` | 构建逻辑：约定任务与扩展函数 | 不放业务代码，详见 `build-layout.md` |
| `gradle/` | 主工程版本目录、wrapper、构建脚本片段 | 根 `build.gradle.kts` 通过 `apply(from=...)` 引入 |
| `scripts/` | 仓库级、可复用的发布 / 构建脚本 | 一次性脚本放 `ai_agents/scripts/` |
| `docs/` | 文档：对外站点 `src/doc`，开发文档 `dev` | 见 `docs/README.md` |
| `home/` | 首页静态站点与前端构建（Node/npm），聚合 docs、demo 产物 | `node_modules/` 已忽略 |
| `data/` | 本地运行数据库文件（`rss.db`） | 不得提交数据库文件 |
| `.github/` | CI/CD 工作流、Issue/PR 模板、CODEOWNERS | 改动工作流需说明影响 |
| `kotlin-js-store/` | Kotlin/JS & Wasm 依赖锁文件 | 构建自动生成，勿手改 |
| `ai_agents/` | **非项目文件唯一允许存放地** | 已 gitignore，不参与构建与提交 |

## 3. 模块清单（`settings.gradle.kts` 实际 include 的）

```
:app:webApp                 wasmJs 浏览器端入口
:app:desktopApp             JVM 桌面端入口
:app:androidApp             Android 入口（assembleDebug 可产出 APK）

:client:composer            客户端组装 / 依赖注入
:client:contract:data       客户端数据契约（接口、模型）
:client:contract:all        契约聚合出口
:client:impl-data:network   契约实现：Ktor 网络
:client:impl-data:mock      契约实现：内存 mock
:client:impl-data:local     契约实现：本地持久化
:client:ui:app              UI 应用装配 / 根组合
:client:ui:components       公共 UI 组件（当前仅有构建脚本，待填充）
:client:ui:navigation       页面导航
:client:ui:feature:feed     业务功能：信息流
:client:ui:theme            Compose 主题、字体、资源

:common:domain              领域模型（纯 Kotlin，无框架依赖）
:common:shared              通用工具与基础能力
:common:api                 对外 API 契约 / DTO
:common:business            业务逻辑（依赖 domain、shared）
:common:foundation          基础设施封装（日志、配置等）

:server:application         应用层 / 用例（依赖 common:domain、common:business）
:server:infrastructure      技术实现（持久化等）
:server:adaptor             适配层（HTTP 入口等）
:server:bootstrap           启动与装配，唯一可执行入口

:home                       首页站点
:docs                       文档站点
```

## 4. 依赖方向（单向，禁止逆流）

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

硬性规则：

- `common:domain` 不依赖任何其他模块，也不引入框架依赖
- `common:*` 不得依赖 `client:*` / `server:*` / `app:*`
- UI 层只依赖 `client:contract:*`，**不得直接依赖 `impl-data:*`**；实现由 `client:composer` 装配

## 5. 新增模块的三步

1. `settings.gradle.kts` 中 `include(":a:b")`
2. 需要新依赖/插件版本时，补**根** `gradle/libs.versions.toml`
3. 在本文 §3 登记职责与依赖关系；若影响分层规则，同步 `AGENTS.md` §0 与本文 §4
