package com.haddouche.episodeo.models

/**
 * Jerarquía de clases para unificar todos los tipos de listas en la aplicación.
 * Esto permite tratar de forma polimórfica tanto a las listas del sistema (generadas dinámicamente)
 * como a las listas personalizadas (guardadas en base de datos).
 */
sealed class AppList {
    abstract val id: String
    abstract val name: String
    abstract val seriesIds: List<Int>
}

/**
 * Representa una lista generada por el sistema basada en el estado de las series.
 * Ejemplos: "Viendo", "Pendiente", "Terminada".
 * No se guarda en base de datos como documento, se calcula en tiempo de ejecución.
 */
data class SystemList(
    override val id: String, // Ej: "watching", "pending"
    override val name: String,
    override val seriesIds: List<Int>
) : AppList()

/**
 * Representa una lista personalizada creada por el usuario.
 * Se mapea desde la entidad `UserList` de la base de datos.
 */
data class CustomList(
    override val id: String,
    override val name: String,
    override val seriesIds: List<Int>,
    val ownerId: String,
    val isPublic: Boolean = false
) : AppList() {
    fun toUserList() = UserList(id, name, ownerId, isPublic, seriesIds)
}
