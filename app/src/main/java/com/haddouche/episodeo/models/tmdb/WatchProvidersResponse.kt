package com.haddouche.episodeo.models.tmdb

import com.squareup.moshi.Json

/**
 * Modela la respuesta de "watch/providers".
 */
data class WatchProvidersResponse(
    val results: Map<String, WatchProviderCountry> = emptyMap()
)

/**
 * Modela los proveedores para un país específico (ej: "ES").
 */
data class WatchProviderCountry(
    // "flatrate" son las plataformas de suscripción tipo Netflix, HBO, etc.
    val flatrate: List<WatchProvider> = emptyList()
)

/**
 * Modela un proveedor de streaming específico.
 */
data class WatchProvider(
    @Json(name = "provider_name")
    val providerName: String,

    @Json(name = "logo_path")
    val logoPath: String?
)
