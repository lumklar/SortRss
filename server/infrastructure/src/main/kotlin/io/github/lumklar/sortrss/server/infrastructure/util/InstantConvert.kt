// TimeConversion.kt
package io.github.lumklar.sortrss.server.infrastructure.util

import kotlin.time.Instant as KotlinInstant
import java.time.Instant as JavaInstant

/**
 * 将 Kotlin 的 Instant 转换为 Java 的 Instant
 */
fun KotlinInstant.toJavaInstant(): JavaInstant =
    JavaInstant.ofEpochSecond(this.epochSeconds, this.nanosecondsOfSecond.toLong())

/**
 * 将 Java 的 Instant 转换为 Kotlin 的 Instant
 */
fun JavaInstant.toKotlinInstant(): KotlinInstant =
    KotlinInstant.fromEpochSeconds(this.epochSecond, this.nano.toLong())

// ---------- 可空版本（常用） ----------
fun KotlinInstant?.toJavaInstantOrNull(): JavaInstant? = this?.toJavaInstant()
fun JavaInstant?.toKotlinInstantOrNull(): KotlinInstant? = this?.toKotlinInstant()