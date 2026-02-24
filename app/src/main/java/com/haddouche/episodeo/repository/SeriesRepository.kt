package com.haddouche.episodeo.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.haddouche.episodeo.data.local.SeriesDao
import com.haddouche.episodeo.data.local.entities.CachedSeriesEntity
import com.haddouche.episodeo.data.local.entities.FollowedListEntity
import com.haddouche.episodeo.data.local.entities.UserListEntity
import com.haddouche.episodeo.data.local.entities.UserSeriesEntity
import com.haddouche.episodeo.models.AppList
import com.haddouche.episodeo.models.CustomList
import com.haddouche.episodeo.models.FollowedList
import com.haddouche.episodeo.models.ShareCode
import com.haddouche.episodeo.models.SystemList
import com.haddouche.episodeo.models.UserList
import com.haddouche.episodeo.models.UserSeries
import com.haddouche.episodeo.models.tmdb.TmdbSeries
import com.haddouche.episodeo.network.TmdbApi
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SeriesRepository @Inject constructor(
    private val dao: SeriesDao,
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    private val userId: String?
        get() = auth.currentUser?.uid

    // --- FUNCIONES DE SERIES (METADATA Y CACHE) ---
    
    suspend fun getSeriesDetails(id: Int): TmdbSeries? {
        // 1. Intentar Red (Online)
        try {
            val series = TmdbApi.service.getSeriesDetails(id)
            // Guardar en Cache Local
            dao.insertCachedSeries(CachedSeriesEntity(
                id = series.id,
                name = series.name,
                posterPath = series.posterPath,
                synopsis = series.synopsis,
                releaseDate = series.releaseDate
            ))
            return series
        } catch (e: Exception) {
            // 2. Fallback a Cache Local (Offline)
            val cached = dao.getCachedSeries(id) ?: return null
            return TmdbSeries(
                id = cached.id,
                name = cached.name,
                synopsis = cached.synopsis,
                posterPath = cached.posterPath,
                releaseDate = cached.releaseDate,
                credits = null, 
                watchProviders = null
            )
        }
    }

    // --- FUNCIONES DE COMPARTIR CON CÓDIGOS ---

    suspend fun generateAndSaveCode(listId: String): String {
        val uid = userId ?: throw IllegalStateException("Usuario no autenticado")
        // Solo online
        try {
            val code = UUID.randomUUID().toString().replace("-", "").substring(0, 6).uppercase()
            val codeRef = ShareCode(
                code = code,
                ownerId = uid,
                listId = listId
            )
            db.collection("codigos_compartir").document(code).set(codeRef).await()
            return code
        } catch (e: Exception) {
            throw Exception("Necesitas conexión para compartir listas.")
        }
    }

    suspend fun findListByCode(code: String): UserList? {
        try {
            val codeRef = db.collection("codigos_compartir").document(code).get().await()
                .toObject(ShareCode::class.java) ?: return null
            return loadListById(codeRef.ownerId, codeRef.listId)
        } catch (e: Exception) {
            throw Exception("Necesitas conexión para importar listas.")
        }
    }


    // --- FUNCIONES DE SERIES INDIVIDUALES (ESTADO USUARIO) ---
    suspend fun loadUserSeries(): Map<Int, UserSeries> {
        val uid = userId ?: return emptyMap()
        
        // 1. Cargar de Local (Lectura permitida offline)
        val localEntities = dao.getUserSeries(uid)
        val localMap = localEntities.associate { 
            it.seriesId to UserSeries(it.seriesStatus, it.seriesRating) 
        }

        // 2. Intentar sincronizar con Remoto
        try {
            val snapshot = db.collection("usuarios/$uid/series").get().await()
            val remoteMap = snapshot.mapNotNull { document ->
                document.id.toIntOrNull()?.let { id ->
                    val status = document.getString("seriesStatus") ?: return@let null
                    val rating = document.getLong("seriesRating")?.toInt()
                    UserSeriesEntity(
                        seriesId = id,
                        seriesStatus = status,
                        seriesRating = rating,
                        userId = uid
                    )
                }
            }
            
            // CLOUD PREVAILS: solo sincronizar si el remoto tiene datos
            if (remoteMap.isNotEmpty()) {
                dao.clearUserSeries(uid)
                remoteMap.forEach { dao.insertUserSeries(it) }
                return remoteMap.associate { it.seriesId to UserSeries(it.seriesStatus, it.seriesRating) }
            }
            return localMap
            
        } catch (e: Exception) {
            return localMap
        }
    }

    suspend fun saveStatus(seriesId: Int, newStatus: String) {
        val uid = userId ?: return
        
        try {
            // SOLO ONLINE
            val seriesData = mapOf("seriesStatus" to newStatus)
            db.document("usuarios/$uid/series/$seriesId")
                .set(seriesData, SetOptions.merge()).await()
            
            val currentEntity = dao.getUserSeries(uid).find { it.seriesId == seriesId }
            val newEntity = UserSeriesEntity(
                seriesId = seriesId,
                seriesStatus = newStatus,
                seriesRating = currentEntity?.seriesRating,
                userId = uid
            )
            dao.insertUserSeries(newEntity)
        } catch (e: Exception) {
            throw Exception("No hay conexión. No se pueden guardar cambios.")
        }
    }

    suspend fun removeSeriesStatus(seriesId: Int) {
        val uid = userId ?: return
        try {
            db.document("usuarios/$uid/series/$seriesId").delete().await()
            // Reconstruir caché local sin esta serie
            val remaining = dao.getUserSeries(uid).filter { it.seriesId != seriesId }
            dao.clearUserSeries(uid)
            remaining.forEach { dao.insertUserSeries(it) }
        } catch (e: Exception) {
            throw Exception("No hay conexión. No se pueden guardar cambios.")
        }
    }

    suspend fun saveRating(seriesId: Int, newRating: Int?) {
        val uid = userId ?: return

        try {
            val docRef = db.document("usuarios/$uid/series/$seriesId")
            if (newRating != null) {
                val seriesData = mapOf("seriesRating" to newRating)
                docRef.set(seriesData, SetOptions.merge()).await()
            } else {
                docRef.update("seriesRating", null).await()
            }

            val currentEntity = dao.getUserSeries(uid).find { it.seriesId == seriesId }
            val status = currentEntity?.seriesStatus ?: "pending"

            val newEntity = UserSeriesEntity(
                seriesId = seriesId,
                seriesStatus = status,
                seriesRating = newRating,
                userId = uid
            )
            dao.insertUserSeries(newEntity)
        } catch (e: Exception) {
            throw Exception("No hay conexión. No se pueden guardar cambios.")
        }
    }

    // --- FUNCIONES DE LISTAS ---

    suspend fun getAllLists(): List<AppList> {
        val uid = userId ?: return emptyList()

        // 1. Cargar Listas Personalizadas (Firestore con fallback a local)
        val customLists: List<CustomList>
        try {
            val remoteLists = loadMyListsFromFirestore()
            dao.clearUserLists(uid)
            remoteLists.forEach { list ->
                dao.insertUserList(UserListEntity(
                    id = list.id,
                    name = list.name,
                    ownerId = list.ownerId ?: uid,
                    isPublic = list.isPublic,
                    seriesIds = list.seriesIds,
                    userId = uid
                ))
            }
            customLists = remoteLists.map {
                CustomList(it.id, it.name, it.seriesIds, it.ownerId ?: uid, it.isPublic)
            }
        } catch (e: Exception) {
            val localListEntities = dao.getUserLists(uid)
            return buildSystemAndCustomLists(localListEntities.map {
                CustomList(it.id, it.name, it.seriesIds, it.ownerId, it.isPublic)
            })
        }

        return buildSystemAndCustomLists(customLists)
    }

    private suspend fun buildSystemAndCustomLists(customLists: List<CustomList>): List<AppList> {
        val userSeriesMap = loadUserSeries()
        val systemLists = mutableListOf<SystemList>()

        val statusMap = mapOf(
            "watching" to "Viendo",
            "pending" to "Pendiente",
            "completed" to "Terminada",
            "dropped" to "Abandonada"
        )

        statusMap.forEach { (statusKey, statusName) ->
            val ids = userSeriesMap.filter { entry ->
                entry.value.seriesStatus == statusKey || entry.value.seriesStatus == statusName
            }.keys.toList()
            if (ids.isNotEmpty()) {
                systemLists.add(SystemList(statusKey, statusName, ids))
            }
        }

        return systemLists + customLists
    }

    private suspend fun loadMyListsFromFirestore(): List<UserList> {
        val uid = userId ?: return emptyList()
        val snapshot = db.collection("usuarios/$uid/mis_listas").get().await()
        return snapshot.map { document ->
            @Suppress("UNCHECKED_CAST")
            val seriesIds = (document.get("seriesIds") as? List<Long>)?.map { it.toInt() } ?: emptyList()
            UserList(
                id = document.id,
                name = document.getString("name") ?: "",
                ownerId = document.getString("ownerId"),
                isPublic = document.getBoolean("isPublic") ?: false,
                seriesIds = seriesIds
            )
        }
    }

    suspend fun loadFollowedLists(): List<UserList> {
        val uid = userId ?: return emptyList()
        
        try {
            val followedReferences = db.collection("usuarios/$uid/listas_seguidas").get().await()
                .toObjects(FollowedList::class.java)
            
            dao.clearFollowedLists(uid)
            followedReferences.forEach { ref ->
                dao.insertFollowedList(FollowedListEntity(
                    listId = ref.listId,
                    ownerId = ref.ownerId,
                    listName = ref.listName,
                    userId = uid
                ))
            }

            return followedReferences.mapNotNull { reference ->
                loadListById(reference.ownerId, reference.listId)
            }
        } catch (e: Exception) {
             val localEntities = dao.getFollowedLists(uid)
             return emptyList() // Limitación: No tenemos el contenido de listas seguidas offline sin una tabla extra
        }
    }

    suspend fun loadListById(ownerId: String, listId: String): UserList? {
        try {
            return db.document("usuarios/$ownerId/mis_listas/$listId")
                .get().await().toObject(UserList::class.java)
        } catch (e: Exception) {
            return null
        }
    }


    suspend fun createList(listName: String) {
        val uid = userId ?: return

        try {
            val newDocRef = db.collection("usuarios/$uid/mis_listas").document()
            val newId = newDocRef.id
            val newListData = mapOf(
                "name" to listName,
                "seriesIds" to emptyList<Int>(),
                "ownerId" to uid,
                "isPublic" to false
            )
            newDocRef.set(newListData).await()

            val newListEntity = UserListEntity(
                id = newId,
                name = listName,
                ownerId = uid,
                isPublic = false,
                seriesIds = emptyList(),
                userId = uid
            )
            dao.insertUserList(newListEntity)
        } catch (e: Exception) {
            throw Exception("No hay conexión. No se pueden crear listas.")
        }
    }

    suspend fun addSeriesToList(seriesId: Int, listName: String) {
        val uid = userId ?: return

        try {
            val snapshot = db.collection("usuarios/$uid/mis_listas")
                .whereEqualTo("name", listName).get().await()
            if (snapshot.isEmpty) return
            val docRef = snapshot.documents[0].reference
            docRef.update("seriesIds", FieldValue.arrayUnion(seriesId)).await()

            val localLists = dao.getUserLists(uid)
            val targetList = localLists.find { it.name == listName }
            if (targetList != null) {
                dao.insertUserList(targetList.copy(seriesIds = targetList.seriesIds + seriesId))
            }
        } catch (e: Exception) {
            throw Exception("No hay conexión. No se pueden hacer cambios.")
        }
    }

    suspend fun removeSeriesFromList(seriesId: Int, listName: String) {
        val uid = userId ?: return

        try {
            val snapshot = db.collection("usuarios/$uid/mis_listas")
                .whereEqualTo("name", listName).get().await()
            if (snapshot.isEmpty) return
            val docRef = snapshot.documents[0].reference
            docRef.update("seriesIds", FieldValue.arrayRemove(seriesId)).await()

            val localLists = dao.getUserLists(uid)
            val targetList = localLists.find { it.name == listName }
            if (targetList != null) {
                dao.insertUserList(targetList.copy(seriesIds = targetList.seriesIds - seriesId))
            }
        } catch (e: Exception) {
            throw Exception("No hay conexión. No se pueden hacer cambios.")
        }
    }

    suspend fun deleteList(listName: String) {
        val uid = userId ?: return

        try {
            val snapshot = db.collection("usuarios/$uid/mis_listas")
                .whereEqualTo("name", listName).get().await()
            if (snapshot.isEmpty) return
            val docRef = snapshot.documents[0].reference
            val docId = snapshot.documents[0].id
            docRef.delete().await()
            dao.deleteUserList(docId)
        } catch (e: Exception) {
            throw Exception("No hay conexión. No se puede eliminar la lista.")
        }
    }

    suspend fun followList(list: UserList) {
        val uid = userId ?: return
        
        try {
            val reference = FollowedList(
                ownerId = list.ownerId ?: "",
                listId = list.id,
                listName = list.name
            )
            db.collection("usuarios/$uid/listas_seguidas").document(list.id).set(reference).await()
            
            val entity = FollowedListEntity(
                listId = list.id,
                ownerId = list.ownerId ?: "",
                listName = list.name,
                userId = uid
            )
            dao.insertFollowedList(entity)
        } catch (e: Exception) {
            throw Exception("No hay conexión. No se puede seguir la lista.")
        }
    }

    suspend fun unfollowList(listId: String) {
        val uid = userId ?: return
        
        try {
            db.collection("usuarios/$uid/listas_seguidas").document(listId).delete().await()
            dao.deleteFollowedList(listId)
        } catch (e: Exception) {
            throw Exception("No hay conexión. No se puede dejar de seguir.")
        }
    }

    suspend fun deleteAccount() {
        val uid = userId ?: return
        
        try {
            db.collection("usuarios/$uid/series").get().await().documents.forEach { it.reference.delete().await() }
            db.collection("usuarios/$uid/mis_listas").get().await().documents.forEach { it.reference.delete().await() }
            db.collection("usuarios/$uid/listas_seguidas").get().await().documents.forEach { it.reference.delete().await() }
            db.document("usuarios/$uid").delete().await()
            auth.currentUser?.delete()?.await()

            dao.clearUserSeries(uid)
            dao.clearUserLists(uid)
            dao.clearFollowedLists(uid)
        } catch (e: Exception) {
            throw Exception("No hay conexión. No se puede eliminar la cuenta.")
        }
    }
}
