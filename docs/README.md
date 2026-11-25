# docs —— 文档目录

本目录只放两类文档，**互不混放**：

| 路径 | 读者 | 内容 | 是否进 MkDocs 站点 |
| --- | --- | --- | --- |
| `src/doc/` | 使用者（外部） | 安装、使用、功能说明、版本记录 | ✅ 是，唯一站点源码 |
| `dev/` | 开发者 / AI 代理 | 架构、构建、脚本、发布、开发约束 | ❌ 否，只在仓库里读 |

判定方法：

- 读者是“**用**这个软件的人” → `src/doc/docs/`
- 读者是“**改**这个仓库的人”（含 AI） → `dev/`

## `src/doc/` 结构

```
src/doc/
├── mkdocs.yml          # MkDocs + Material 配置、nav、mike 版本化
├── requirements.txt    # mkdocs-material、mike
├── scripts/hooks.py    # on_config 钩子：从环境变量注入 repo_url / site_url / version
└── docs/               # 站点正文，新增页面需同步 mkdocs.yml 的 nav
    ├── index.md
    ├── guide/          # 面向用户的操作指南
    └── about/          # 版本记录等
```

构建与部署见 `dev/build-and-release.md`。

## `dev/` 文档索引

| 文档 | 内容 |
| --- | --- |
| [dev/architecture.md](dev/architecture.md) | 顶层目录职责、模块清单、依赖方向与分层规则 |
| [dev/build-layout.md](dev/build-layout.md) | `scripts/`、`gradle/`、`buildSrc/` 逐文件/逐目录用途 |
| [dev/build-and-release.md](dev/build-and-release.md) | 常用构建命令、校验任务、Docker/发布产物、签名、CI 工作流 |
| [dev/versioning.md](dev/versioning.md) | 两套版本目录的分工、同步规则、版本检查 |
| [dev/ai-workflow.md](dev/ai-workflow.md) | `ai_agents/` 工作区规则、命名、转正流程、提交前检查 |

## 其他

- `build.gradle.kts`：`:docs` 模块构建脚本（mkdocsBuild → build/dist，mike 部署任务）。
- `TODO.md`：文档站与构建的待办，不属于正式文档。
- `build/`：构建产物，勿手改、勿提交。
