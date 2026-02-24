package com.haddouche.episodeo.models

/**
 * Este modelo representa el Documento de un usuario
 * que estara guardado en la coleccion "usuarios".
 * Usamos los nombres en español como dijimos.
 */
data class User(
    val email: String = "",
    val username: String = "",
    val darkTheme: Boolean = false
)