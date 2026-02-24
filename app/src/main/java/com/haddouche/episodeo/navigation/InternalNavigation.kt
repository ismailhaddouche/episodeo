package com.haddouche.episodeo.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.haddouche.episodeo.screens.*
import com.haddouche.episodeo.viewmodels.HomeViewModel

@Composable
fun InternalNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    loginNavController: NavController,
    homeViewModel: HomeViewModel,
    showSearchBar: Boolean,
    onAddToListClick: (Int) -> Unit,
    onSeriesClick: (Int) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = AppRoutes.HomeScreen.route,
        modifier = modifier
    ) {
        composable(AppRoutes.HomeScreen.route) {
            HomeScreen(
                homeViewModel = homeViewModel,
                showSearchBar = showSearchBar,
                onAddToListClick = onAddToListClick,
                onSeriesClick = onSeriesClick
            )
        }
        composable(AppRoutes.MyListsScreen.route) {
            MyListsScreen(
                navController = navController,
                homeViewModel = homeViewModel,
                onSeriesClick = onSeriesClick
            )
        }
        composable(AppRoutes.FollowedListsScreen.route) {
            FollowedListsScreen(
                navController = navController,
                homeViewModel = homeViewModel
            )
        }
        composable(AppRoutes.StatusScreen.route) {
            val statusName = it.arguments?.getString("statusName") ?: ""
            StatusScreen(
                statusName = statusName,
                homeViewModel = homeViewModel,
                onAddToListClick = onAddToListClick,
                onSeriesClick = onSeriesClick
            )
        }
        composable(
            route = AppRoutes.ListDetailScreen.route,
            arguments = listOf(navArgument("listName") { type = NavType.StringType })
        ) { backStackEntry ->
            val listName = backStackEntry.arguments?.getString("listName") ?: ""
            ListDetailScreen(
                listName = listName,
                homeViewModel = homeViewModel,
                onNavigateBack = { navController.popBackStack() },
                onAddToListClick = onAddToListClick,
                onSeriesClick = onSeriesClick
            )
        }
        composable(AppRoutes.SettingsScreen.route) {
            SettingsScreen(
                onNavigateUp = { navController.popBackStack() },
                onNavigateToLogin = {
                    loginNavController.navigate(AppRoutes.LoginScreen.route) {
                        popUpTo(0) {
                            inclusive = true
                        }
                    }
                },
                // ¡AQUÍ ESTÁ LA CLAVE!
                // Pasamos el mismo ViewModel que recibe InternalNavigation (que viene de MainScreen, que viene de MainActivity)
                // Así todos comparten el mismo estado del tema.
                homeViewModel = homeViewModel
            )
        }
    }
}
