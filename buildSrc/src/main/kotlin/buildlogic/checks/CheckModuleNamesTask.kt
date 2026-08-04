package buildlogic.checks

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

abstract class CheckModuleNamesTask : DefaultTask() {

    @Input
    lateinit var moduleNameToPaths: Map<String, List<String>>
        private set

    fun setModuleNameToPaths(moduleNameToPaths: Map<String, List<String>>) {
        this.moduleNameToPaths = moduleNameToPaths
    }

    init {
        group = "verification"
        description = "检查所有子模块名称是否唯一"
    }

    @TaskAction
    fun check() {
        val duplicates = moduleNameToPaths.filter { it.value.size > 1 }
        if (duplicates.isNotEmpty()) {
            val count = duplicates.size
            logger.error("❌ Found $count duplicate module name(s).")
            val errorMsg = duplicates.entries.joinToString("\n") { (name, paths) ->
                "  - Module name '$name' is duplicated in: ${paths.joinToString(", ")}"
            }
            errorMsg.lines().forEach { logger.error(it) }
            throw GradleException("Module name uniqueness check failed: $count duplicate module name(s) found.")
        } else {
            logger.lifecycle("✅ All module names are unique.")
        }
    }
}
