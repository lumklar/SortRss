package io.github.lumklar.sortrss.common.domain.model.article

import kotlin.jvm.JvmInline
import kotlin.uuid.Uuid

@JvmInline
value class ArticleId(val value: Uuid)