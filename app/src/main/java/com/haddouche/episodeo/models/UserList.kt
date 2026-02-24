package com.haddouche.episodeo.models

import com.google.firebase.firestore.DocumentId

/**
 * Representa una lista de series creada por un usuario dentro de la subcolección `mis_listas`.
 *
 * @property id ID único del documento, autogenerado por Firestore.
 * @property name Nombre de la lista, definido por el usuario.
 * @property ownerId UID del usuario que creó la lista. Es nullable para mantener la
 *                       compatibilidad con versiones anteriores del modelo de datos.
 * @property isPublic Flag para futuras implementaciones de visibilidad de listas. Actualmente no utilizado.
 * @property seriesIds Lista de IDs de TMDB de las series contenidas en esta lista.
 */
data class UserList(
    @DocumentId
    val id: String = "",
    val name: String = "",
    val ownerId: String? = null,
    val isPublic: Boolean = false,
    val seriesIds: List<Int> = emptyList()
)
