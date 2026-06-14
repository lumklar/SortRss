package io.github.lumklar.sortrss.client.composer

import io.github.lumklar.sortrss.client.contract.all.AllContractFactory
import io.github.lumklar.sortrss.client.contract.data.DataContractFactory
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class FlavorAllContractFactory(
) : AllContractFactory, KoinComponent {
    private val dataFactory: DataContractFactory by inject()
    override fun data(): DataContractFactory = dataFactory
}
