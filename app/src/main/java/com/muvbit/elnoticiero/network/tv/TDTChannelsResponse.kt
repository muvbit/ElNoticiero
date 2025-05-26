package com.muvbit.elnoticiero.network.tv

import retrofit2.http.GET

// Respuesta completa de la API
data class TDTChannelsResponse(
    val version: String,
    val country: String,
    val bouquets: List<Bouquet>,
    val channels: List<TDTChannel>
)

// Grupo de canales (bouquet)
data class Bouquet(
    val name: String,
    val channels: List<String> // Lista de channel_id
)

// Datos del canal
data class TDTChannel(
    val channel_id: String,
    val name: String,
    val epg_id: String?,
    val country: String,
    val website: String?,
    val logo: String?,
    val streams: List<Stream>,
    val categories: List<String>?
)

// Stream del canal
data class Stream(
    val url: String,
    val http_referrer: String?,
    val user_agent: String?,
    val is_hd: Boolean = false
)

// Interfaz para Retrofit (sin cambios)
interface TDTChannelsService {
    @GET("lists/tv.json")
    suspend fun getTVChannels(): TDTChannelsResponse
}