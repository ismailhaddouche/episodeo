package com.haddouche.episodeo.screens

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.auth.FirebaseAuth
import com.haddouche.episodeo.components.SearchResultItem
import com.haddouche.episodeo.viewmodels.ListDetailState
import com.haddouche.episodeo.viewmodels.HomeViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListDetailScreen(
    listName: String,
    homeViewModel: HomeViewModel,
    onNavigateBack: () -> Unit,
    onAddToListClick: (Int) -> Unit,
    onSeriesClick: (Int) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = listName) {
        homeViewModel.loadListAndSeries(listName)
    }

    val state = homeViewModel.listDetailState
    val seriesInList = homeViewModel.seriesInSelectedList
    val userSeries = homeViewModel.userSeries

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val title = when (state) {
                        is ListDetailState.Success -> state.list.name
                        is ListDetailState.Error -> "Error"
                        else -> "Cargando..."
                    }
                    Text(title)
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver atrás")
                    }
                },
                actions = {
                    if (state is ListDetailState.Success) {
                        val list = state.list
                        if (homeViewModel.isOwnList(list)) {
                            // --- ACCIONES PARA EL PROPIETARIO ---
                            IconButton(onClick = {
                                scope.launch {
                                    val code = homeViewModel.generateCodeForList(list.id)
                                    val currentUser = FirebaseAuth.getInstance().currentUser?.displayName ?: "Alguien"
                                    val shareText = """
                                        $currentUser quiere compartir la lista '${list.name}' contigo.
                                        
                                        Usa el código $code para poder seguir la lista en Episodeo.
                                    """.trimIndent()

                                    val intent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_TEXT, shareText)
                                    }
                                    context.startActivity(Intent.createChooser(intent, "Compartir lista con..."))
                                }
                            }) {
                                Icon(Icons.Default.Share, contentDescription = "Compartir lista")
                            }
                            IconButton(onClick = {
                                homeViewModel.deleteList(list.name)
                                onNavigateBack()
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar lista")
                            }
                        } else {
                            // --- ACCIONES PARA OTROS USUARIOS ---
                            val isFollowing = homeViewModel.isFollowingList(list.id)
                            if (isFollowing) {
                                Button(onClick = { homeViewModel.unfollowList(list.id) }) {
                                    Text("Dejar de Seguir")
                                }
                            } else {
                                Button(onClick = { homeViewModel.followList(list) }) {
                                    Text("Seguir")
                                }
                            }
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            when (state) {
                is ListDetailState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is ListDetailState.Error -> {
                    Text(
                        text = state.message,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is ListDetailState.Success -> {
                    if (seriesInList.isEmpty()) {
                        Text(
                            text = "No hay series en esta lista.",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(seriesInList) { series ->
                                SearchResultItem(
                                    series = series,
                                    currentStatus = userSeries[series.id]?.seriesStatus,
                                    currentRating = userSeries[series.id]?.seriesRating,
                                    onStatusSelected = { id, status -> homeViewModel.saveSeriesStatus(id, status) },
                                    onRatingSelected = { id, rating -> homeViewModel.saveSeriesRating(id, rating) },
                                    onAddToListClick = onAddToListClick,
                                    onRemoveFromListClick = if (homeViewModel.isOwnList(state.list)) {
                                        { id -> homeViewModel.removeSeriesFromList(id, state.list.name) }
                                    } else null,
                                    onSeriesClick = onSeriesClick
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}