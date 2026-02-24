package com.haddouche.episodeo.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.haddouche.episodeo.models.tmdb.CastMember
import com.haddouche.episodeo.models.tmdb.TmdbSeries
import com.haddouche.episodeo.models.tmdb.WatchProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeriesDetailDialog(
    series: TmdbSeries,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(series.name, maxLines = 1) },
                    navigationIcon = { IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, "Cerrar") } }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // --- INFO GENERAL Y PÓSTER ---
                Row(modifier = Modifier.fillMaxWidth()) {
                    val imageUrl = if (series.posterPath != null) "https://image.tmdb.org/t/p/w342${series.posterPath}" else null
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Carátula de ${series.name}",
                        modifier = Modifier.width(120.dp).aspectRatio(2/3f).clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(series.name, style = MaterialTheme.typography.headlineSmall)
                        Text("Año: ${series.releaseDate?.substring(0, 4) ?: "N/A"}", style = MaterialTheme.typography.bodyLarge)
                    }
                }

                // --- PLATAFORMAS ---
                val platforms = series.watchProviders?.results?.get("ES")?.flatrate
                if (!platforms.isNullOrEmpty()) {
                    DetailSection(title = "Disponible en")
                    LazyRow {
                        items(platforms) { platform ->
                            PlatformItem(platform)
                        }
                    }
                }

                // --- SINOPSIS ---
                DetailSection(title = "Sinopsis")
                Text(series.synopsis, style = MaterialTheme.typography.bodyMedium)

                // --- DIRECTORES Y CREADORES ---
                val directors = series.credits?.crew?.filter { it.job == "Director" || it.job == "Creator" }?.distinctBy { it.name }
                if (!directors.isNullOrEmpty()) {
                    DetailSection(title = "Dirección y Creación")
                    Text(directors.joinToString { it.name }, style = MaterialTheme.typography.bodyMedium)
                }

                // --- ACTORES ---
                val actors = series.credits?.cast?.take(10) // Mostramos los 10 primeros
                if (!actors.isNullOrEmpty()) {
                    DetailSection(title = "Reparto Principal")
                    LazyRow {
                        items(actors) { actor ->
                            ActorItem(actor)
                        }
                    }
                }
            }
        }
    }
}

// --- COMPONENTES DE AYUDA ---

@Composable
fun DetailSection(title: String) {
    Spacer(modifier = Modifier.height(16.dp))
    Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun PlatformItem(platform: WatchProvider) {
    val logoUrl = if (platform.logoPath != null) "https://image.tmdb.org/t/p/w92${platform.logoPath}" else null
    AsyncImage(
        model = logoUrl,
        contentDescription = platform.providerName,
        modifier = Modifier.padding(end = 12.dp).size(50.dp).clip(RoundedCornerShape(8.dp))
    )
}

@Composable
fun ActorItem(actor: CastMember) {
    val photoUrl = if (actor.profilePath != null) "https://image.tmdb.org/t/p/w185${actor.profilePath}" else null

    Column(
        modifier = Modifier
            .padding(end = 8.dp)
            .width(100.dp),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = photoUrl,
            contentDescription = "Foto de ${actor.name}",
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape), // Hacemos la foto redonda
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = actor.name,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 2, // Permitimos dos líneas por si el nombre es largo
            textAlign = TextAlign.Center
        )
        Text(
            text = actor.character,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 2,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
