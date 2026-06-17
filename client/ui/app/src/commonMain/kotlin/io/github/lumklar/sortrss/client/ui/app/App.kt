package io.github.lumklar.sortrss.client.ui.app

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import io.github.lumklar.sortrss.client.contract.all.AllContractFactory
import io.github.lumklar.sortrss.client.ui.navigation.AppNavHost
import io.github.lumklar.sortrss.client.ui.theme.AppTheme

@Composable
fun App(
    factory: AllContractFactory,  // 只依赖 composer 提供的工厂
    modifier: Modifier = Modifier
) {
    AppTheme {
        val navController = rememberNavController()

        // 【关键】app 只传 factory，不传任何 ViewModel
        AppNavHost(
            navController = navController,
            factory = factory,      // 传给 navigation
            modifier = modifier
        )
    }
}
