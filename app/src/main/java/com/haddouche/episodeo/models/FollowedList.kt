package com.haddouche.episodeo.models

import com.google.firebase.firestore.DocumentId

/**
 * Representa un documento en la subcolección `listas_seguidas` de un usuario.
 * Este modelo no contiene la lista en sí, sino una referencia a la lista original
 * de otro usuario para evitar duplicidad de datos.
 *
 * @property id ID autogenerado del documento que representa esta relación de seguimiento.
 * @property ownerId UID del usuario propietario de la lista original.
 * @property listId ID del documento de la lista en la subcolección `mis_listas` del propietario.
 * @property listName Nombre de la lista original. Se almacena aquí para un acceso rápido
 *                       sin necesidad de consultar el documento original.
 */
data class FollowedList(
    @DocumentId
    val id: String = "",
    val ownerId: String = "",
    val listId: String = "",
    val listName: String = ""
)
