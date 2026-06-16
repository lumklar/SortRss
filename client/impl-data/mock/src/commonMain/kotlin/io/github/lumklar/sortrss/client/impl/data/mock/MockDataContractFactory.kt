package io.github.lumklar.sortrss.client.impl.data.mock

import io.github.lumklar.sortrss.client.contract.data.DataContractFactory

class MockDataContractFactory : DataContractFactory {
    override fun hello(): String {
        return "mock-impl"
    }
}