package com.haddouche.episodeo.models

/**
 * Representa el estado de una serie específica para un usuario.
 * Cada documento de este tipo se almacena en la subcolección `series`
 * del documento de un usuario.
 *
 * @property seriesStatus Estado actual de visionado de la serie (ej: "Viendo", "Pendiente").
 * @property seriesRating Calificación personal que el usuario ha asignado a la serie (nullable).
 */
data class UserSeries(
    val seriesStatus: String = "pending",
    val seriesRating: Int? = null
)
