package com.haddouche.episodeo.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.haddouche.episodeo.components.FollowListDialog
import com.haddouche.episodeo.navigation.AppRoutes
import com.haddouche.episodeo.viewmodels.HomeViewModel

@Composable
fun FollowedListsScreen(
    navController: NavController,
    homeViewModel: HomeViewModel
) {
    val followedLists = homeViewModel.followedLists
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    if (showDialog) {
        FollowListDialog(
            onDismissRequest = { showDialog = false },
            onConfirm = { code ->
                homeViewModel.followListByCode(code) { success, message ->
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
                showDialog = false
            }
        )
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Visibility, contentDescription = "Seguir una lista")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            if (followedLists.isEmpty()) {
                Text(
                    "Aún no sigues ninguna lista. Pulsa el ojo para añadir una con un código.",
                    modifier = Modifier.align(Alignment.Center).padding(16.dp)
                )
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(followedLists) { list ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .clickable {
                                    navController.navigate(AppRoutes.ListDetailScreen.createRoute(list.name))
                                }
                        ) {
                            Text(
                                text = "${list.name} (${list.seriesIds.size} series)",
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
