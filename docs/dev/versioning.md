# 版本管理

## 1. 两套版本目录，各自独立

| 文件 | 管什么 | 在谁的脚本里是 `libs` |
| --- | --- | --- |
| `gradle/libs.versions.toml` | 主工程全部模块（`:app:*`、`:client:*`、`:common:*`、`:server:*`、`:home`、`:docs`）以及根 `build.gradle.kts` 的插件版本 | 根项目与各子模块 |
| `buildSrc/gradle/libs.versions.toml` | `buildSrc` 自身构建逻辑用到的依赖与插件（`kotlin-dsl`、`commons-compress`、`xz`、`zstd-jni`…） | **仅** `buildSrc/build.gradle.kts` |

`buildSrc` 是独立构建，**读不到根版本目录**。`buildSrc/settings.gradle.kts` 里“从
`../gradle/libs.versions.toml` 导入 `libs`”的代码当前是注释状态；确有需要再启用，
启用后以根目录为准，避免两处维护。

## 2. 重复条目必须同步升级

两个版本的重复项：

| 条目 | 主工程 | buildSrc |
| --- | --- | --- |
| `ben-manes-versions` | 0.62.0 | 0.62.0 |
| `commons-compress` | 1.28.0 | 1.28.0 |

只改一边，会让“依赖检查插件版本”和“构建逻辑实际使用的版本”漂移，进而出现
`NoSuchMethodError` 之类难以定位的问题。改版本时**两处一起改**。

## 3. 改版本先认门

| 要改的版本 | 改哪里 |
| --- | --- |
| 模块依赖 / 根插件 | `gradle/libs.versions.toml` |
| `buildSrc/src` 用到的库、`kotlin-dsl` 插件 | `buildSrc/gradle/libs.versions.toml` |
| BOM 临时安全覆盖（如 `tomcat.version`） | 别名写根版本目录，映射写 `gradle/version-overrides.properties` |

模块脚本一律用 `alias(libs.xxx)` / `libs.xxx` 引用，**禁止硬编码版本号**；
`checkDependencyVersions` 与 `checkPluginVersions` 会扫描全部 `build.gradle(.kts)` 做校验。

临时安全覆盖不再写在 `build.gradle.kts` 的 `extra[...]` 里：`gradle/version-overrides.properties`
只声明 `BOM 属性名=版本目录别名`，由 `gradle/version-overrides.gradle.kts` 写入 `extra`。
`checkVersionOverrides` 会解析当前 Spring Boot BOM；一旦 BOM 自带版本不低于覆盖版本，
构建失败并要求同时删除配置和版本目录别名，避免老版本被长期钉住。

## 4. 工具链

- `refreshVersions`（`settings.gradle.kts`，当前 0.60.6）：`gradlew refreshVersions` 生成升级建议。
  根 `versions.properties` 由该插件维护，**不要手改或拆分以 `####` 开头的注释**。
- `ben-manes.versions`：`gradlew dependencyUpdates` 只查 release 版，输出 HTML 报告。
- `dependency-check-jvm` 已在版本目录登记，冲突/未使用检查仍为 TODO（见 `docs/TODO.md`）。

## 5. 新增依赖的完整步骤

1. 在 `gradle/libs.versions.toml` 的 `[versions]` / `[libraries]`（或 `[plugins]`）登记
2. 模块 `build.gradle.kts` 用 `implementation(libs.xxx)` / `alias(libs.plugins.xxx)`
3. 若 `buildSrc` 也要用，另在 `buildSrc/gradle/libs.versions.toml` 登记，并保持版本一致
4. `gradlew checkDependencyVersions checkPluginVersions checkConfigKeys checkVersionOverrides` 通过后再提交
