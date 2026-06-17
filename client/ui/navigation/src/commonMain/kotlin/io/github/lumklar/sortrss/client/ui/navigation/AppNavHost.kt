package io.github.lumklar.sortrss.client.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import io.github.lumklar.sortrss.client.contract.all.AllContractFactory
import io.github.lumklar.sortrss.client.ui.feature.feed.HomeScreen
import io.github.lumklar.sortrss.client.ui.feature.feed.UserViewModel
import kotlinx.serialization.Serializable

@Serializable
object HomeRoute

@Composable
fun AppNavHost(
    navController: NavHostController,
    factory: AllContractFactory,   // 接收工厂，而不是 ViewModel 实例
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = HomeRoute,
        modifier = modifier
    ) {
        composable<HomeRoute> {
            // 【关键】在这里创建 ViewModel，生命周期绑定当前路由
            val userRepo = remember { factory.data().userRepository() }
            val viewModel = remember { UserViewModel(userRepo) }

            HomeScreen(viewModel = viewModel)
        }

        // 后续新增页面，都在这里创建自己的 ViewModel
        // composable<DetailRoute> {
        //     val feedRepo = remember { factory.data().feedRepository() }
        //     val detailVm = remember { DetailViewModel(feedRepo) }
        //     DetailScreen(detailVm)
        // }
    }
}
