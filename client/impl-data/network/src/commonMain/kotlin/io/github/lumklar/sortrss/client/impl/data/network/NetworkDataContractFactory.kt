package io.github.lumklar.sortrss.client.impl.data.network

import io.github.lumklar.sortrss.client.contract.data.DataContractFactory
import io.github.lumklar.sortrss.client.contract.data.repository.UserRepository

class NetworkDataContractFactory : DataContractFactory {
    override fun hello(): String {
        return "network-impl"
    }

    override fun userRepository(): UserRepository {
        TODO("Not yet implemented")
    }
}