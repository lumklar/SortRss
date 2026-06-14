package io.github.lumklar.sortrss.client.composer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import io.github.lumklar.sortrss.client.impl.data.spi.dataFactoryModule
import io.github.lumklar.sortrss.client.ui.UI
import org.koin.core.context.startKoin

@Composable
fun CLIENT() {
    startKoin{
        //TODO 统一约定模块名称
        modules(dataFactoryModule)
    }
    val allFactory = remember {
        FlavorAllContractFactory()
    }
    UI(factory = allFactory)
}