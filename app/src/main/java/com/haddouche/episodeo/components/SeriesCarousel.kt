package com.haddouche.episodeo.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.haddouche.episodeo.models.tmdb.TmdbSeries

/**
 * Componente que muestra una lista de series en un carrusel horizontal con un título.
 */
@Composable
fun SeriesCarousel(
    title: String,
    series: List<TmdbSeries>,
    onSeriesClick: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Título de la sección (ej: "Viendo")
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp, top = 16.dp)
        )
        
        // Carrusel Horizontal
        LazyRow(
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(series) { seriesItem ->
                SavedSeriesItem(
                    series = seriesItem,
                    onSeriesClick = onSeriesClick
                )
            }
        }
    }
}
