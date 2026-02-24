package com.haddouche.episodeo.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.haddouche.episodeo.components.SearchResultItem
import com.haddouche.episodeo.components.statusDisplayNames
import com.haddouche.episodeo.viewmodels.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusScreen(
    statusName: String,
    homeViewModel: HomeViewModel,
    onAddToListClick: (Int) -> Unit,
    onSeriesClick: (Int) -> Unit
) {
    val seriesInStatus = homeViewModel.getSeriesByStatus(statusName)
    val userSeries = homeViewModel.userSeries

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Series: ${statusDisplayNames[statusName] ?: statusName} (${seriesInStatus.size})") })
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            items(seriesInStatus, key = { it.id }) { series ->
                val userSeriesInfo = userSeries[series.id]
                SearchResultItem(
                    series = series,
                    currentStatus = userSeriesInfo?.seriesStatus,
                    currentRating = userSeriesInfo?.seriesRating,
                    onStatusSelected = { seriesId, newStatus ->
                        homeViewModel.saveSeriesStatus(seriesId, newStatus)
                    },
                    onRatingSelected = { seriesId, newRating ->
                        homeViewModel.saveSeriesRating(seriesId, newRating)
                    },
                    onAddToListClick = onAddToListClick,
                    onRemoveFromListClick = null,
                    onSeriesClick = onSeriesClick
                )
            }
        }
    }
}
