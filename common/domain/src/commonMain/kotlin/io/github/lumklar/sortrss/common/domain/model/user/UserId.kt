package io.github.lumklar.sortrss.common.domain.model.user

import kotlin.jvm.JvmInline
import kotlin.uuid.Uuid

@JvmInline
value class UserId(val value: Uuid) {
}