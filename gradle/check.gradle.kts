tasks.register("checkModuleNames") {
    // 在配置阶段收集：只收集“有构建脚本”的子模块
    val nameMap = mutableMapOf<String, MutableList<String>>()
    subprojects.forEach { sub ->
        // 检查该模块是否有 build.gradle.kts 或 build.gradle
        val hasBuildFile = sub.buildFile.exists() || sub.file("build.gradle").exists()
        if (hasBuildFile) {
            nameMap.getOrPut(sub.name) { mutableListOf() }.add(sub.path)
        }
    }

    doLast {
        val duplicates = nameMap.filter { it.value.size > 1 }
        if (duplicates.isEmpty()) {
            println("✅ All module names are unique.")
        } else {
            val errorMsg = duplicates.entries.joinToString("\n") { (name, paths) ->
                "  - Module name '$name' is duplicated in: ${paths.joinToString(", ")}"
            }
            throw GradleException("Duplicate module names detected:\n$errorMsg")
        }
    }
}