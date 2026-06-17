package io.github.lumklar.sortrss.client.impl.data.local

import io.github.lumklar.sortrss.client.contract.data.DataContractFactory
import io.github.lumklar.sortrss.client.contract.data.repository.UserRepository

class LocalDataContractFactory : DataContractFactory {
    override fun hello(): String {
        return "local-impl"
    }

    override fun userRepository(): UserRepository {
        TODO("Not yet implemented")
    }
}