package io.github.lumklar.sortrss.client.impl.data.mock

import io.github.lumklar.sortrss.client.contract.data.DataContractFactory
import io.github.lumklar.sortrss.client.contract.data.repository.UserRepository

class MockDataContractFactory : DataContractFactory {
    private val userRepo = MockUserRepository()

    override fun hello(): String = "Mock World"

    override fun userRepository(): UserRepository = userRepo
}
