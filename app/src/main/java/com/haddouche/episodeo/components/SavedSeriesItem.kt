package com.haddouche.episodeo.components

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.haddouche.episodeo.models.tmdb.TmdbSeries

/**
 * Componente que muestra la carátula de una serie para un carrusel.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedSeriesItem(
    series: TmdbSeries,
    onSeriesClick: (Int) -> Unit
) {
    val imageUrl = if (series.posterPath != null) {
        "https://image.tmdb.org/t/p/w342${series.posterPath}" // Usamos una imagen de más calidad
    } else {
        null
    }

    Card(
        onClick = { onSeriesClick(series.id) },
        modifier = Modifier.padding(end = 8.dp)
    ) {
        // Usamos AsyncImage para cargar la carátula
        AsyncImage(
            model = imageUrl,
            contentDescription = "Carátula de ${series.name}",
            // El aspectRatio mantiene la proporción 2:3 de los pósters
            modifier = Modifier
                .width(120.dp)
                .aspectRatio(2 / 3f),
            contentScale = ContentScale.Crop
        )
    }
}
