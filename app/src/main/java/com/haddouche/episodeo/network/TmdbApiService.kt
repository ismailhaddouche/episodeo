package com.haddouche.episodeo.network

import com.haddouche.episodeo.BuildConfig
import com.haddouche.episodeo.models.tmdb.TmdbSearchResponse
import com.haddouche.episodeo.models.tmdb.TmdbSeries
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

private const val URL_BASE = "https://api.themoviedb.org/3/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val httpClient = OkHttpClient.Builder()
    .connectTimeout(10, TimeUnit.SECONDS)
    .readTimeout(15, TimeUnit.SECONDS)
    .writeTimeout(10, TimeUnit.SECONDS)
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(URL_BASE)
    .client(httpClient)
    .build()

interface TmdbApiService {

    @GET("search/tv")
    suspend fun searchSeries(
        @Query("query") query: String,
        @Query("api_key") apiKey: String = BuildConfig.CLAVE_API_TMDB,
        @Query("language") language: String = "es-ES"
    ): TmdbSearchResponse

    // --- ¡FUNCIÓN MEJORADA! ---
    @GET("tv/{tv_id}")
    suspend fun getSeriesDetails(
        @Path("tv_id") seriesId: Int,
        @Query("api_key") apiKey: String = BuildConfig.CLAVE_API_TMDB,
        @Query("language") language: String = "es-ES",
        // Le pedimos a la API que incluya los créditos y los proveedores en la misma llamada
        @Query("append_to_response") appendToResponse: String = "credits,watch/providers"
    ): TmdbSeries
}

object TmdbApi {
    val service: TmdbApiService by lazy {
        retrofit.create(TmdbApiService::class.java)
    }
}
