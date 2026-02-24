package com.haddouche.episodeo.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.haddouche.episodeo.navigation.AppRoutes
import com.haddouche.episodeo.viewmodels.HomeViewModel

@Composable
fun MyListsScreen(
    navController: NavController,
    homeViewModel: HomeViewModel,
    onSeriesClick: (Int) -> Unit
) {
    val customLists = homeViewModel.getCustomLists()

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(customLists, key = { it.id }) { lista ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clickable { 
                        navController.navigate(AppRoutes.ListDetailScreen.createRoute(lista.name)) 
                    }
            ) {
                Text(
                    text = "${lista.name} (${lista.seriesIds.size} series)",
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}
