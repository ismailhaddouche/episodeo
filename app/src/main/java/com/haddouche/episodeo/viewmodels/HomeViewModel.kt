package com.haddouche.episodeo.viewmodels

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.imageLoader
import coil.request.ImageRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.haddouche.episodeo.models.AppList
import com.haddouche.episodeo.models.CustomList
import com.haddouche.episodeo.models.SystemList
import com.haddouche.episodeo.models.UserList
import com.haddouche.episodeo.models.UserSeries
import com.haddouche.episodeo.models.tmdb.TmdbSeries
import com.haddouche.episodeo.network.TmdbApi
import com.haddouche.episodeo.repository.SeriesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

sealed class ListDetailState {
    object Loading : ListDetailState()
    data class Success(val list: AppList) : ListDetailState()
    data class Error(val message: String) : ListDetailState()
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: SeriesRepository,
    @ApplicationContext private val context: Context,
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) : ViewModel() {

    // Dispatcher para operaciones IO — sobreescribible en tests
    @Suppress("MemberVisibilityCanBePrivate")
    internal var ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    // --- ESTADOS DE LA UI ---
    var searchText by mutableStateOf("")
        private set
    var searchResults by mutableStateOf<List<TmdbSeries>>(emptyList())
        private set
    
    // Nueva estructura unificada
    var allLists by mutableStateOf<List<AppList>>(emptyList())
        private set
    
    var isDashboardLoading by mutableStateOf(false)
        private set
    var isSearchLoading by mutableStateOf(false)
        private set
    val userSeries = mutableStateMapOf<Int, UserSeries>()
    var errorMessage by mutableStateOf<String?>(null)
        private set
    
    var followedLists by mutableStateOf<List<UserList>>(emptyList())
        private set
    var seriesInSelectedList by mutableStateOf<List<TmdbSeries>>(emptyList())
        private set
    var seriesInDetail by mutableStateOf<TmdbSeries?>(null)
        private set

    var seriesByStatus by mutableStateOf<Map<String, List<TmdbSeries>>>(emptyMap())
        private set

    // --- ESTADO DEL TEMA ---
    var isDarkTheme by mutableStateOf(true) // Por defecto oscuro
        private set

    var listDetailState by mutableStateOf<ListDetailState>(ListDetailState.Loading)
        private set

    // --- FUNCIONES AUXILIARES ---
    
    fun getSystemLists(): List<SystemList> = allLists.filterIsInstance<SystemList>()
    
    fun getCustomLists(): List<CustomList> = allLists.filterIsInstance<CustomList>()
    
    fun getCustomListsAsUserList(): List<UserList> = getCustomLists().map {
        UserList(it.id, it.name, it.ownerId, it.isPublic, it.seriesIds)
    }

    fun getSeriesByStatus(status: String): List<TmdbSeries> {
        return seriesByStatus[status] ?: emptyList()
    }

    // --- LÓGICA DE COMPARTIR ---
    suspend fun generateCodeForList(listId: String): String {
        return repository.generateAndSaveCode(listId)
    }

    fun followListByCode(code: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val list = repository.findListByCode(code)
                if (list != null) {
                    repository.followList(list)
                    followedLists = repository.loadFollowedLists()
                    onResult(true, "¡Has empezado a seguir la lista '${list.name}'!")
                } else {
                    onResult(false, "El código introducido no es válido.")
                }
            } catch (e: Exception) {
                onResult(false, "Error: ${e.message}")
            }
        }
    }


    // --- LÓGICA PRINCIPAL ---

    fun changeTheme(isDark: Boolean) {
        isDarkTheme = isDark
        viewModelScope.launch {
            try {
                val uid = auth.currentUser?.uid
                if (uid != null) {
                    db.document("usuarios/$uid")
                        .set(mapOf("isDarkTheme" to isDark), SetOptions.merge())
                }
            } catch (e: Exception) {
                // Ignorar offline
            }
        }
    }

    fun loadListAndSeries(listName: String) {
        viewModelScope.launch {
            listDetailState = ListDetailState.Loading
            try {
                val userListsFromFollowed = followedLists.map { 
                    CustomList(it.id, it.name, it.seriesIds, it.ownerId ?: "", it.isPublic) 
                }
                val list = (allLists + userListsFromFollowed).find { it.name == listName }

                if (list != null) {
                    val seriesWithDetails = list.seriesIds.map {
                        async { repository.getSeriesDetails(it) }
                    }.awaitAll().filterNotNull()
                    
                    seriesInSelectedList = seriesWithDetails
                    listDetailState = ListDetailState.Success(list)
                } else {
                    listDetailState = ListDetailState.Error("Lista no encontrada")
                }
            } catch (e: Exception) {
                listDetailState = ListDetailState.Error("No hay conexión a internet para cargar los detalles.")
            }
        }
    }

    fun loadInitialData() {
        viewModelScope.launch {
            isDashboardLoading = true
            errorMessage = null
            try {
                // Cargar tema
                try {
                    val uid = auth.currentUser?.uid
                    if (uid != null) {
                        val doc = db.document("usuarios/$uid").get().await()
                        if (doc.exists()) {
                            isDarkTheme = doc.getBoolean("isDarkTheme") ?: true
                        }
                    }
                } catch (e: Exception) { /* Offline ignora esto */ }

                // Cargar datos
                val savedSeriesJob = async { repository.loadUserSeries() }
                val allListsJob = async { repository.getAllLists() }
                val followedListsJob = async { repository.loadFollowedLists() }

                val savedSeries = savedSeriesJob.await()
                allLists = allListsJob.await()
                followedLists = followedListsJob.await()

                userSeries.clear()
                userSeries.putAll(savedSeries)
                
                updateSeriesByStatus()

                // --- DESCARGA AUTOMÁTICA DE METADATOS Y CARÁTULAS ---
                downloadAllMetadataAndImages()

            } catch (e: Exception) {
                errorMessage = "Error al cargar el dashboard: ${e.message}"
            } finally {
                isDashboardLoading = false
            }
        }
    }
    
    private fun downloadAllMetadataAndImages() {
        viewModelScope.launch(ioDispatcher) {
            val allIds = mutableSetOf<Int>()

            allIds.addAll(userSeries.keys)
            allLists.forEach { list ->
                if (list is CustomList) allIds.addAll(list.seriesIds)
            }
            followedLists.forEach { list ->
                allIds.addAll(list.seriesIds)
            }

            // Descargar en lotes paralelos de 5 para no saturar la red
            allIds.chunked(5).forEach { batch ->
                batch.map { id ->
                    async {
                        try {
                            val series = repository.getSeriesDetails(id)
                            if (series?.posterPath != null) {
                                val imageUrl = "https://image.tmdb.org/t/p/w342${series.posterPath}"
                                val request = ImageRequest.Builder(context)
                                    .data(imageUrl)
                                    .build()
                                context.imageLoader.enqueue(request)
                            }
                        } catch (e: Exception) {
                            // Ignorar fallos individuales
                        }
                    }
                }.awaitAll()
            }
        }
    }
    
    private suspend fun updateSeriesByStatus() {
        val idsByStatus = userSeries.entries.groupBy({ it.value.seriesStatus }, { it.key })
        val newSeriesByStatus = mutableMapOf<String, List<TmdbSeries>>()

        try {
            coroutineScope {
                idsByStatus.forEach { (status, ids) ->
                    val details = ids.map { id ->
                        async {
                            try {
                                repository.getSeriesDetails(id)
                            } catch (e: Exception) {
                                null
                            }
                        }
                    }.awaitAll().filterNotNull()
                    newSeriesByStatus[status] = details
                }
            }
            seriesByStatus = newSeriesByStatus
        } catch (e: Exception) {
            // Offline handled
        }
    }

    fun onSearchTextChanged(newText: String) {
        searchText = newText
        if (newText.isBlank()) {
            searchResults = emptyList()
        }
    }

    fun searchSeries() {
        viewModelScope.launch {
            isSearchLoading = true
            errorMessage = null
            try {
                val response = TmdbApi.service.searchSeries(query = searchText)
                searchResults = response.results
            } catch (e: Exception) {
                errorMessage = "No hay conexión a internet. No se puede buscar."
                searchResults = emptyList()
            } finally {
                isSearchLoading = false
            }
        }
    }

    fun saveSeriesStatus(seriesId: Int, newStatus: String) {
        viewModelScope.launch {
            val previousSeries = userSeries[seriesId]

            // "none" = el usuario quita el estado: eliminar de Firestore y del mapa local
            if (newStatus == "none") {
                userSeries.remove(seriesId)
                try {
                    repository.removeSeriesStatus(seriesId)
                    allLists = repository.getAllLists()
                } catch (e: Exception) {
                    if (previousSeries != null) userSeries[seriesId] = previousSeries
                    errorMessage = e.message
                }
                return@launch
            }

            val existingSeries = previousSeries ?: UserSeries()
            // Optimistic Update
            userSeries[seriesId] = existingSeries.copy(seriesStatus = newStatus)

            try {
                repository.saveStatus(seriesId, newStatus)
                loadInitialData()
            } catch (e: Exception) {
                if (previousSeries != null) {
                    userSeries[seriesId] = previousSeries
                } else {
                    userSeries.remove(seriesId)
                }
                errorMessage = e.message
            }
        }
    }

    fun saveSeriesRating(seriesId: Int, newRating: Int?) {
        viewModelScope.launch {
            val previousSeries = userSeries[seriesId]
            val existingSeries = previousSeries ?: UserSeries()

            userSeries[seriesId] = existingSeries.copy(seriesRating = newRating)

            try {
                repository.saveRating(seriesId, newRating)
                allLists = repository.getAllLists()
            } catch (e: Exception) {
                if (previousSeries != null) {
                    userSeries[seriesId] = previousSeries
                } else {
                    userSeries.remove(seriesId)
                }
                errorMessage = e.message
            }
        }
    }

    fun addSeriesToList(seriesId: Int, listName: String) {
        viewModelScope.launch {
            try {
                repository.addSeriesToList(seriesId, listName)
                allLists = repository.getAllLists()
            } catch (e: Exception) {
                 errorMessage = e.message
            }
        }
    }

    fun createListAndAddSeries(seriesId: Int, listName: String) {
        viewModelScope.launch {
            try {
                repository.createList(listName)
                repository.addSeriesToList(seriesId, listName)
                allLists = repository.getAllLists()
            } catch (e: Exception) {
                 errorMessage = e.message
            }
        }
    }

    fun removeSeriesFromList(seriesId: Int, listName: String) {
        viewModelScope.launch {
            try {
                repository.removeSeriesFromList(seriesId, listName)
                loadInitialData()
            } catch (e: Exception) {
                 errorMessage = e.message
            }
        }
    }


    fun showSeriesDetails(seriesId: Int) {
        viewModelScope.launch {
            try {
                val details = repository.getSeriesDetails(seriesId)
                if (details != null) {
                    seriesInDetail = details
                } else {
                    errorMessage = "No hay conexión y no está en cache."
                }
            } catch (e: Exception) {
                errorMessage = "No hay conexión a internet."
            }
        }
    }

    fun hideSeriesDetails() {
        seriesInDetail = null
    }

    fun deleteList(listName: String) {
        viewModelScope.launch {
            try {
                repository.deleteList(listName)
                allLists = repository.getAllLists()
            } catch (e: Exception) {
                 errorMessage = e.message
            }
        }
    }

    fun followList(list: AppList) {
        viewModelScope.launch {
            try {
                if (list is CustomList) {
                    val userList = UserList(list.id, list.name, list.ownerId, list.isPublic, list.seriesIds)
                    repository.followList(userList)
                    followedLists = repository.loadFollowedLists()
                    listDetailState = ListDetailState.Success(list)
                }
            } catch (e: Exception) {
                 errorMessage = e.message
            }
        }
    }

    fun unfollowList(listId: String) {
        viewModelScope.launch {
            try {
                repository.unfollowList(listId)
                followedLists = repository.loadFollowedLists()
                allLists = repository.getAllLists()
                listDetailState = ListDetailState.Loading
            } catch (e: Exception) {
                errorMessage = e.message
            }
        }
    }

    fun isOwnList(list: AppList): Boolean {
        return when (list) {
            is CustomList -> list.ownerId == auth.currentUser?.uid
            is SystemList -> true
        }
    }

    fun isFollowingList(listId: String): Boolean {
        return followedLists.any { it.id == listId }
    }

    fun deleteAccount(onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                repository.deleteAccount()
                onComplete()
            } catch (e: Exception) {
                errorMessage = e.message
            }
        }
    }
}
