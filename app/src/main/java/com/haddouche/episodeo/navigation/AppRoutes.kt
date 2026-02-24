package com.haddouche.episodeo.navigation

sealed class AppRoutes(val route: String) {
    object LoginScreen : AppRoutes("login")
    object MainScreen : AppRoutes("principal") // Contenedor principal

    // --- Rutas internas de PantallaPrincipal ---
    object HomeScreen : AppRoutes("home")
    object MyListsScreen : AppRoutes("mis_listas")
    object FollowedListsScreen : AppRoutes("listas_seguidas") // <-- ¡NUEVA RUTA!
    object StatusScreen : AppRoutes("estado/{statusName}") {
        fun createRoute(statusName: String) = "estado/$statusName"
    }
    object ListDetailScreen : AppRoutes("lista/{listName}") {
        fun createRoute(listName: String) = "lista/$listName"
    }
    object SettingsScreen : AppRoutes("ajustes")
}
