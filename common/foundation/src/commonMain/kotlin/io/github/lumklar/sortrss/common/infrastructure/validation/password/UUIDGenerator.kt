package io.github.lumklar.sortrss.common.infrastructure.validation.password

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class UUIDGenerator {
    @OptIn(ExperimentalUuidApi::class)
    fun generateBytes(): ByteArray {
        return Uuid.generateV7().toByteArray()
    }

    /**
     * 生成一个 UUIDv7 字符串（如 "0192f5cd-0c2e-7a3f-b1d2-8b9a6d2b4c11"）
     */
    @OptIn(ExperimentalUuidApi::class)
    fun generate(): String {
        return Uuid.generateV7().toString()
    }

    /**
     * 如果需要直接操作 Uuid 对象，可以返回它
     */
    @OptIn(ExperimentalUuidApi::class)
    fun generateUuid(): Uuid {
        return Uuid.generateV7()
    }
}