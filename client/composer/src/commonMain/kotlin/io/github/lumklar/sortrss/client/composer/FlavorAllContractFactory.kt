package io.github.lumklar.sortrss.client.composer

import io.github.lumklar.sortrss.client.contract.all.AllContractFactory
import io.github.lumklar.sortrss.client.contract.data.DataContractFactory
import io.github.lumklar.sortrss.client.impl.data.network.DataContractNetworkFactory


class FlavorAllContractFactory(
    private val dataFactory: DataContractFactory = DataContractNetworkFactory()
) : AllContractFactory {
    override fun data(): DataContractFactory = dataFactory
}
