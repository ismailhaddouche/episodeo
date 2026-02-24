package com.haddouche.episodeo.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.haddouche.episodeo.models.tmdb.TmdbSeries
import com.haddouche.episodeo.ui.theme.RatingActiveColor
import com.haddouche.episodeo.ui.theme.StatusCompletedColor
import com.haddouche.episodeo.ui.theme.StatusDroppedColor
import com.haddouche.episodeo.ui.theme.StatusNoneColor
import com.haddouche.episodeo.ui.theme.StatusPendingColor
import com.haddouche.episodeo.ui.theme.StatusWatchingColor

// --- Constantes de estado para evitar errores de escritura ---
// --- Constants for status ---
const val STATUS_PENDING = "pending"
const val STATUS_WATCHING = "watching"
const val STATUS_COMPLETED = "completed"
const val STATUS_DROPPED = "dropped"
const val STATUS_NONE = "none"

val statusDisplayNames = mapOf(
    STATUS_PENDING to "Pendiente",
    STATUS_WATCHING to "Viendo",
    STATUS_COMPLETED to "Terminada",
    STATUS_DROPPED to "Abandonada",
    STATUS_NONE to "Sin Estado"
)

val statusOptions = listOf(
    STATUS_PENDING, STATUS_WATCHING, STATUS_COMPLETED, STATUS_DROPPED, STATUS_NONE
)

const val OPTION_NO_RATING = "Sin puntuar"
val ratingOptions = listOf(OPTION_NO_RATING) + (1..10).map { it.toString() }

/**
 * Componente reutilizable para mostrar una serie en una lista.
 * Es el "ladrillo" para la búsqueda, las listas de estado, etc.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultItem(
    series: TmdbSeries,
    currentStatus: String? = null,
    currentRating: Int? = null,
    // Callbacks for events
    onStatusSelected: (seriesId: Int, newStatus: String) -> Unit,
    onRatingSelected: (seriesId: Int, newRating: Int?) -> Unit,
    onAddToListClick: (seriesId: Int) -> Unit,
    onRemoveFromListClick: ((seriesId: Int) -> Unit)? = null,
    onSeriesClick: ((seriesId: Int) -> Unit)? = null
) {
    val imageUrl = if (series.posterPath != null) "https://image.tmdb.org/t/p/w185${series.posterPath}" else null

    var statusExpanded by remember { mutableStateOf(false) }
    var ratingExpanded by remember { mutableStateOf(false) }

    // Synchronize local state with external state
    var selectedStatus by remember { mutableStateOf(currentStatus ?: STATUS_NONE) }
    LaunchedEffect(currentStatus) {
        selectedStatus = currentStatus ?: STATUS_NONE
    }

    // Determine icon and color based on status
    val (statusIcon, statusIconColor) = when (selectedStatus) {
        STATUS_WATCHING  -> Icons.Default.Visibility        to StatusWatchingColor
        STATUS_PENDING   -> Icons.Default.HourglassTop      to StatusPendingColor
        STATUS_DROPPED   -> Icons.Default.Cancel            to StatusDroppedColor
        STATUS_COMPLETED -> Icons.Default.Check             to StatusCompletedColor
        else             -> Icons.Default.RemoveCircleOutline to StatusNoneColor
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(enabled = onSeriesClick != null) { 
                onSeriesClick?.invoke(series.id)
            }
    ) {
        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Carátula de ${series.name}",
                modifier = Modifier.size(width = 80.dp, height = 120.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = series.name,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))

                // --- FILA DE BOTONES COMPLETA Y RESTAURADA ---
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // 1. Botón de Estado
                    Box {
                        IconButton(onClick = { statusExpanded = true }) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(statusIcon, "Estado: ${statusDisplayNames[selectedStatus]}", tint = statusIconColor)
                                Icon(Icons.Default.ArrowDropDown, "Seleccionar estado")
                            }
                        }
                        DropdownMenu(expanded = statusExpanded, onDismissRequest = { statusExpanded = false }) {
                            statusOptions.forEach { status ->
                                DropdownMenuItem(text = { Text(statusDisplayNames[status] ?: status) }, onClick = {
                                    selectedStatus = status
                                    statusExpanded = false
                                    onStatusSelected(series.id, status)
                                })
                            }
                        }
                    }

                    // 2. Botón de Añadir a Lista
                    IconButton(onClick = { onAddToListClick(series.id) }) {
                        Icon(Icons.AutoMirrored.Filled.PlaylistAdd, contentDescription = "Añadir a lista")
                    }

                    // 3. Botón de Quitar de Lista (Opcional)
                    if (onRemoveFromListClick != null) {
                        IconButton(onClick = { onRemoveFromListClick(series.id) }) {
                            Icon(Icons.Default.RemoveCircle, contentDescription = "Quitar de la lista", tint = MaterialTheme.colorScheme.error)
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // 4. Botón de Puntuación
                    Box {
                        Row(
                            modifier = Modifier.clickable { ratingExpanded = true },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (currentRating != null) {
                                Text(text = currentRating.toString(), fontWeight = FontWeight.Bold, color = RatingActiveColor)
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Puntuación: ${currentRating ?: OPTION_NO_RATING}",
                                tint = if (currentRating != null) RatingActiveColor else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Icon(Icons.Default.ArrowDropDown, "Seleccionar puntuación")
                        }
                        DropdownMenu(expanded = ratingExpanded, onDismissRequest = { ratingExpanded = false }) {
                            ratingOptions.forEach { option ->
                                DropdownMenuItem(text = { Text(option) }, onClick = {
                                    ratingExpanded = false
                                    val newRating = if (option == OPTION_NO_RATING) null else option.toIntOrNull()
                                    onRatingSelected(series.id, newRating)
                                })
                            }
                        }
                    }
                }
            }
        }
    }
}
