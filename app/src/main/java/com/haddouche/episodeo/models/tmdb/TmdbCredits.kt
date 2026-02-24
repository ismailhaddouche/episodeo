package com.haddouche.episodeo.models.tmdb

import com.squareup.moshi.Json

/**
 * Modela la respuesta completa del endpoint de "credits".
 */
data class TmdbCredits(
    val cast: List<CastMember> = emptyList(),
    val crew: List<CrewMember> = emptyList()
)

/**
 * Modela a un miembro del reparto (un actor/actriz).
 */
data class CastMember(
    val id: Int,
    val name: String,
    val character: String,
    // --- ¡NUEVO CAMPO! ---
    @Json(name = "profile_path")
    val profilePath: String? // La ruta a la foto del actor
)

/**
 * Modela a un miembro del equipo técnico.
 */
data class CrewMember(
    val id: Int,
    val name: String,
    val job: String
)
