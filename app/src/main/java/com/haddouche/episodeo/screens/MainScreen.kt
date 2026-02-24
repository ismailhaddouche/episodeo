package com.haddouche.episodeo.screens

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.haddouche.episodeo.components.AddToListDialog
import com.haddouche.episodeo.components.HomeTopAppBar
import com.haddouche.episodeo.components.SearchBar
import com.haddouche.episodeo.components.SearchResultItem
import com.haddouche.episodeo.components.SeriesDetailDialog
import com.haddouche.episodeo.components.SideMenu
import com.haddouche.episodeo.navigation.AppRoutes
import com.haddouche.episodeo.navigation.InternalNavigation
import com.haddouche.episodeo.viewmodels.HomeViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    loginNavController: NavController,
    // Usamos el Activity como ViewModelStoreOwner para compartir el ViewModel con MainActivity
    homeViewModel: HomeViewModel = hiltViewModel(LocalContext.current as ComponentActivity)
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val internalNavController = rememberNavController()
    
    // Estado para la barra de búsqueda
    var showSearchBar by remember { mutableStateOf(false) }
    var seriesIdToAddToList by remember { mutableStateOf<Int?>(null) }

    // Cargar datos iniciales
    LaunchedEffect(key1 = true) {
        homeViewModel.loadInitialData()
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            SideMenu(
                navController = internalNavController,
                loginNavController = loginNavController,
                coroutineScope = coroutineScope,
                closeMenu = { coroutineScope.launch { drawerState.close() } },
                onHomeClick = {
                    coroutineScope.launch { 
                        drawerState.close()
                        internalNavController.navigate(AppRoutes.HomeScreen.route) {
                            popUpTo(AppRoutes.HomeScreen.route) { inclusive = true }
                        }
                    }
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                if (showSearchBar) {
                    SearchBar(
                        text = homeViewModel.searchText,
                        onTextChanged = { homeViewModel.onSearchTextChanged(it) },
                        onSearch = { homeViewModel.searchSeries() },
                        onClose = { 
                            showSearchBar = false
                            homeViewModel.onSearchTextChanged("") // Limpiar búsqueda al cerrar
                        }
                    )
                } else {
                    HomeTopAppBar(
                        onMenuClick = { coroutineScope.launch { drawerState.open() } },
                        onSearchClick = { showSearchBar = true }
                    )
                }
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                // Navegación interna siempre presente en el fondo
                InternalNavigation(
                    navController = internalNavController,
                    loginNavController = loginNavController,
                    homeViewModel = homeViewModel,
                    showSearchBar = showSearchBar, // Mantenemos el parámetro por compatibilidad aunque ya no lo usemos igual
                    onAddToListClick = { seriesId -> seriesIdToAddToList = seriesId },
                    onSeriesClick = { seriesId -> homeViewModel.showSeriesDetails(seriesId) }
                )
                
                // Superposición de resultados de búsqueda
                if (showSearchBar) {
                    GlobalSearchResults(
                        homeViewModel = homeViewModel,
                        onAddToListClick = { seriesId -> seriesIdToAddToList = seriesId },
                        onSeriesClick = { seriesId -> homeViewModel.showSeriesDetails(seriesId) }
                    )
                }
            }

            // Diálogo para añadir a lista
            if (seriesIdToAddToList != null) {
                AddToListDialog(
                    existingLists = homeViewModel.getCustomListsAsUserList(),
                    onDismissRequest = { seriesIdToAddToList = null },
                    onAddToExistingList = { listName ->
                        homeViewModel.addSeriesToList(seriesIdToAddToList!!, listName)
                        seriesIdToAddToList = null
                    },
                    onCreateNewList = { listName ->
                        homeViewModel.createListAndAddSeries(seriesIdToAddToList!!, listName)
                        seriesIdToAddToList = null
                    }
                )
            }

            // Diálogo de detalles de serie
            val seriesInDetail = homeViewModel.seriesInDetail
            if (seriesInDetail != null) {
                SeriesDetailDialog(
                    series = seriesInDetail,
                    onDismiss = { homeViewModel.hideSeriesDetails() }
                )
            }
        }
    }
}

@Composable
fun GlobalSearchResults(
    homeViewModel: HomeViewModel,
    onAddToListClick: (Int) -> Unit,
    onSeriesClick: (Int) -> Unit
) {
    val isSearchLoading = homeViewModel.isSearchLoading
    val searchResults = homeViewModel.searchResults
    val userSeries = homeViewModel.userSeries
    val errorMessage = homeViewModel.errorMessage

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // Fondo sólido para tapar la navegación
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        when {
            isSearchLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
            errorMessage != null && searchResults.isEmpty() -> Text(text = errorMessage, Modifier.align(Alignment.Center))
            searchResults.isEmpty() && homeViewModel.searchText.isNotEmpty() -> Text(text = "No se encontraron resultados", Modifier.align(Alignment.Center).padding(top=20.dp))
            else -> {
                LazyColumn {
                    items(searchResults, key = { it.id }) { series ->
                        SearchResultItem(
                            series = series,
                            currentStatus = userSeries[series.id]?.seriesStatus,
                            currentRating = userSeries[series.id]?.seriesRating,
                            onStatusSelected = { id, status -> homeViewModel.saveSeriesStatus(id, status) },
                            onRatingSelected = { id, rating -> homeViewModel.saveSeriesRating(id, rating) },
                            onAddToListClick = onAddToListClick,
                            onSeriesClick = onSeriesClick
                        )
                    }
                }
            }
        }
    }
}
