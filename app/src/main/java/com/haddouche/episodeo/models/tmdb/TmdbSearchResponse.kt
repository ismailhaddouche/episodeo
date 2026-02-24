package com.haddouche.episodeo.models.tmdb

/**
 * Profe, este es el modelo del objeto "principal"
 * que nos devuelve la API de TMDB cuando buscamos.
 * Solo nos interesa la lista de "results".
 */
data class TmdbSearchResponse(
    val results: List<TmdbSeries>
)
