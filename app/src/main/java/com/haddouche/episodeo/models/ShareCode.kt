package com.haddouche.episodeo.models

import com.google.firebase.firestore.DocumentId

/**
 * Representa un documento en la colección `codigos_compartir` de Firestore.
 * Cada documento funciona como un enlace de un solo uso entre un código y una lista específica.
 *
 * @property code El código alfanumérico único que identifica este documento.
 * @property ownerId El UID del usuario que generó el código y es dueño de la lista.
 * @property listId El ID del documento de la lista en la subcolección `mis_listas` del propietario.
 */
data class ShareCode(
    @DocumentId
    val code: String = "",
    val ownerId: String = "",
    val listId: String = ""
)
