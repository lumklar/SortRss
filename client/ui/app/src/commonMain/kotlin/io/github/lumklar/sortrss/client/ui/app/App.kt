package io.github.lumklar.sortrss.client.ui.app

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import io.github.lumklar.sortrss.client.contract.all.AllContractFactory
import io.github.lumklar.sortrss.client.ui.navigation.AppNavHost
import io.github.lumklar.sortrss.client.ui.theme.AppTheme

@Composable
fun App(
    factory: AllContractFactory,
    modifier: Modifier = Modifier
) {
    AppTheme {
        val navController = rememberNavController()
        AppNavHost(
            navController = navController,
            factory = factory,
            modifier = modifier
        )
    }
}
