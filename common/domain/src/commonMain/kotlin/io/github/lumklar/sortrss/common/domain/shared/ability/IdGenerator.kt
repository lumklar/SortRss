package io.github.lumklar.sortrss.common.domain.shared.ability

interface IdGenerator<T> {
    fun next(): T
}
