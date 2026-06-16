package io.github.lumklar.sortrss.client.impl.data.network

import io.github.lumklar.sortrss.client.contract.data.DataContractFactory

class NetworkDataContractFactory : DataContractFactory {
    override fun hello(): String {
        return "network-impl"
    }
}