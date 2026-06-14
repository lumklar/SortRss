package io.github.lumklar.sortrss.client.composer

import io.github.lumklar.sortrss.client.contract.all.AllContractFactory
import io.github.lumklar.sortrss.client.contract.data.DataContractFactory
import io.github.lumklar.sortrss.client.impl.data.spi.dataFactoryModule
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin


object FlavorAllContractFactory : AllContractFactory, KoinComponent {
    // 懒加载确保 Koin 启动后才获取实例
    private val dataFactory: DataContractFactory by inject()

    init {
        startKoin {
            modules(dataFactoryModule)
        }
    }

    override fun data(): DataContractFactory = dataFactory
}
