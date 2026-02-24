package com.haddouche.episodeo.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.haddouche.episodeo.components.SearchResultItem
import com.haddouche.episodeo.components.SeriesCarousel
import com.haddouche.episodeo.models.SystemList
import com.haddouche.episodeo.models.tmdb.TmdbSeries
import com.haddouche.episodeo.viewmodels.HomeViewModel

/**
 * Pantalla de inicio que muestra el dashboard o los resultados de búsqueda.
 */
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    showSearchBar: Boolean,
    onAddToListClick: (Int) -> Unit,
    onSeriesClick: (Int) -> Unit
) {
    val isDashboardLoading = homeViewModel.isDashboardLoading
    val isSearchLoading = homeViewModel.isSearchLoading
    val errorMessage = homeViewModel.errorMessage
    val systemLists = homeViewModel.getSystemLists()
    val searchResults = homeViewModel.searchResults
    val userSeries = homeViewModel.userSeries

    Box(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        when {
            isDashboardLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
            errorMessage != null -> Text(text = errorMessage, Modifier.align(Alignment.Center))
            // Si la búsqueda está activa, mostramos los resultados
            showSearchBar -> {
                if (isSearchLoading) {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                } else {
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
            // Si no, mostramos el dashboard con las listas del sistema
            else -> {
                SystemListsDashboard(
                    systemLists = systemLists,
                    homeViewModel = homeViewModel,
                    onSeriesClick = onSeriesClick
                )
            }
        }
    }
}

/**
 * Componente que muestra el dashboard con las listas del sistema.
 */
@Composable
private fun SystemListsDashboard(
    systemLists: List<SystemList>,
    homeViewModel: HomeViewModel,
    onSeriesClick: (Int) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item { 
            Text(
                "Mis Series", 
                style = MaterialTheme.typography.headlineMedium, 
                modifier = Modifier.padding(vertical = 16.dp)
            ) 
        }
        
        items(systemLists, key = { it.id }) { systemList ->
            // Ahora obtenemos los datos cacheados del ViewModel directamente
            val series = homeViewModel.getSeriesByStatus(systemList.id)
            
            if (series.isNotEmpty()) {
                SeriesCarousel(
                    title = systemList.name,
                    series = series,
                    onSeriesClick = onSeriesClick
                )
            }
        }
    }
}
