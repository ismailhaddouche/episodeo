package com.haddouche.episodeo.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.haddouche.episodeo.R
import com.haddouche.episodeo.navigation.AppRoutes
import com.haddouche.episodeo.components.STATUS_WATCHING
import com.haddouche.episodeo.components.STATUS_PENDING
import com.haddouche.episodeo.components.STATUS_COMPLETED
import com.haddouche.episodeo.components.STATUS_DROPPED
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun SideMenu(
    navController: NavController,
    loginNavController: NavController,
    coroutineScope: CoroutineScope,
    closeMenu: () -> Unit,
    onHomeClick: () -> Unit
) {
    val context = LocalContext.current

    ModalDrawerSheet {
        MenuHeader()

        // --- SECCIÓN PRINCIPAL ---
        NavigationDrawerItem(
            label = { Text("Home") },
            selected = false,
            onClick = { coroutineScope.launch { closeMenu(); onHomeClick() } },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") }
        )
        NavigationDrawerItem(
            label = { Text("Mis Listas") },
            selected = false,
            onClick = { coroutineScope.launch { closeMenu(); navController.navigate(AppRoutes.MyListsScreen.route) } },
            icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Mis Listas") }
        )
        NavigationDrawerItem(
            label = { Text("Listas Seguidas") },
            selected = false,
            onClick = { coroutineScope.launch { closeMenu(); navController.navigate(AppRoutes.FollowedListsScreen.route) } },
            icon = { Icon(Icons.Default.Bookmark, contentDescription = "Listas Seguidas") }
        )

        HorizontalDivider()

        // --- SECCIÓN DE ESTADOS ---
        NavigationDrawerItem(
            label = { Text("Viendo") },
            selected = false,
            onClick = { coroutineScope.launch { closeMenu(); navController.navigate(AppRoutes.StatusScreen.createRoute(STATUS_WATCHING)) } },
            icon = { Icon(Icons.Default.Visibility, contentDescription = "Viendo") }
        )
        NavigationDrawerItem(
            label = { Text("Pendientes") },
            selected = false,
            onClick = { coroutineScope.launch { closeMenu(); navController.navigate(AppRoutes.StatusScreen.createRoute(STATUS_PENDING)) } },
            icon = { Icon(Icons.Default.HourglassTop, contentDescription = "Pendientes") }
        )
        NavigationDrawerItem(
            label = { Text("Terminadas") },
            selected = false,
            onClick = { coroutineScope.launch { closeMenu(); navController.navigate(AppRoutes.StatusScreen.createRoute(STATUS_COMPLETED)) } },
            icon = { Icon(Icons.Default.Check, contentDescription = "Terminadas") }
        )
        NavigationDrawerItem(
            label = { Text("Abandonadas") },
            selected = false,
            onClick = { coroutineScope.launch { closeMenu(); navController.navigate(AppRoutes.StatusScreen.createRoute(STATUS_DROPPED)) } },
            icon = { Icon(Icons.Default.Cancel, contentDescription = "Abandonadas") }
        )

        HorizontalDivider()

        // --- SECCIÓN DE CUENTA ---
        NavigationDrawerItem(
            label = { Text("Ajustes") },
            selected = false,
            onClick = { coroutineScope.launch { closeMenu(); navController.navigate(AppRoutes.SettingsScreen.route) } },
            icon = { Icon(Icons.Default.Settings, contentDescription = "Ajustes") }
        )
        NavigationDrawerItem(
            label = { Text("Cerrar Sesión") },
            selected = false,
            onClick = {
                coroutineScope.launch {
                    closeMenu()
                    FirebaseAuth.getInstance().signOut()
                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(context.getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build()
                    GoogleSignIn.getClient(context, gso).signOut()
                    loginNavController.navigate(AppRoutes.LoginScreen.route) {
                        popUpTo(0) {
                            inclusive = true
                        }
                    }
                }
            },
            icon = { Icon(Icons.AutoMirrored.Outlined.Logout, contentDescription = "Cerrar Sesión") }
        )
    }
}
