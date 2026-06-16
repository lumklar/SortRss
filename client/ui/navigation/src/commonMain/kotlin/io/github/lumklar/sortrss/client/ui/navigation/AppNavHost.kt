package io.github.lumklar.sortrss.client.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import io.github.lumklar.sortrss.client.contract.all.AllContractFactory
import io.github.lumklar.sortrss.client.ui.feature.feed.HomeScreen
import kotlinx.serialization.Serializable

@Serializable
object HomeRoute

@Composable
fun AppNavHost(
    navController: NavHostController,
    factory: AllContractFactory,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = HomeRoute,
        modifier = modifier
    ) {
        composable<HomeRoute> {
            HomeScreen(dataFactory = factory.data())
        }
        // 后续新增页面仅在此处注册路由即可
    }
}
