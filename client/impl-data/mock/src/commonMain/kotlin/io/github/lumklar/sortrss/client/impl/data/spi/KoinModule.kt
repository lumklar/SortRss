package io.github.lumklar.sortrss.client.impl.data.spi

import io.github.lumklar.sortrss.client.contract.data.DataContractFactory
import io.github.lumklar.sortrss.client.impl.data.mock.MockDataContractFactory
import org.koin.dsl.module

val dataFactoryModule = module {
    single<DataContractFactory> { MockDataContractFactory() }
}
