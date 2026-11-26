package io.github.lumklar.sortrss.server.infrastructure.config.database

import org.springframework.aot.hint.MemberCategory
import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.RuntimeHintsRegistrar
import org.springframework.aot.hint.TypeReference

/**
 * SQLite 社区方言由 Hibernate 按类名反射实例化，AOT 无法从字符串常量推导，
 * native 镜像需要显式注册其构造函数。
 */
class DatabaseNativeHints : RuntimeHintsRegistrar {
    override fun registerHints(hints: RuntimeHints, classLoader: ClassLoader?) {
        hints.reflection().registerType(
            TypeReference.of("org.hibernate.community.dialect.SQLiteDialect"),
            MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS
        )
    }
}
