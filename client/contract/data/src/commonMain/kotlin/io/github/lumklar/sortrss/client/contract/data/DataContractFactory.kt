package io.github.lumklar.sortrss.client.contract.data

import io.github.lumklar.sortrss.client.contract.data.repository.UserRepository

interface DataContractFactory {
    fun hello(): String
    fun userRepository(): UserRepository
}