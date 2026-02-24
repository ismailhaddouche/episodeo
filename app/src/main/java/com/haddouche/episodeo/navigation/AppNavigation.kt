package com.haddouche.episodeo.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.haddouche.episodeo.screens.LoginScreen
import com.haddouche.episodeo.screens.MainScreen

/**
 * Navegador principal de la aplicación.
 * Decide si mostrar el Login o la PantallaPrincipal en función del estado de autenticación.
 */
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val currentUser = FirebaseAuth.getInstance().currentUser

    val startDestination = if (currentUser != null) {
        AppRoutes.MainScreen.route
    } else {
        AppRoutes.LoginScreen.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        composable(AppRoutes.LoginScreen.route) {
            LoginScreen(navController)
        }

        composable(AppRoutes.MainScreen.route) {
            MainScreen(loginNavController = navController)
        }
    }
}
