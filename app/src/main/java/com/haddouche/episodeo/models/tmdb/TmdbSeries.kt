package com.haddouche.episodeo.models.tmdb

import com.squareup.moshi.Json

/**
 * Modelo para UNA serie de TMDB, enriquecido con todos los detalles.
 */
data class TmdbSeries(
    val id: Int,
    val name: String,

    @Json(name = "overview")
    val synopsis: String,

    @Json(name = "poster_path")
    val posterPath: String?,

    @Json(name = "first_air_date")
    val releaseDate: String?,

    // Estos campos los rellenaremos con la llamada "append_to_response"
    val credits: TmdbCredits? = null,

    @Json(name = "watch/providers")
    val watchProviders: WatchProvidersResponse? = null
)
