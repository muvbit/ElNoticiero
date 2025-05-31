package com.muvbit.elnoticiero.network.tv

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET

// network/tv/TDTChannelsResponse.kt
data class TDTChannelsResponse(
    val license: License,
    val epg: Epg,
    val countries: List<Country>
)

data class License(
    val source: String,
    val url: String
)

data class Epg(
    val xml: String,
    @SerializedName("xml.gz") val xmlGz: String,
    val json: String
)

data class Country(
    val name: String,
    val ambits: List<Ambit>
)

data class Ambit(
    val name: String,
    val channels: List<Channel>
)

data class Channel(
    val name: String,
    val web: String?,
    val logo: String?,
    @SerializedName("epg_id") val epgId: String?,
    val options: List<StreamOption>,
    @SerializedName("extra_info") val extraInfo: List<String>?
)

data class StreamOption(
    val format: String,
    val url: String,
    val geo2: String?,
    val res: String?,
    val lang: String?
)

// Interfaz para Retrofit (sin cambios)
interface TDTChannelsService {
    @GET("lists/tv.json")
    suspend fun getTVChannels(): TDTChannelsResponse

    companion object {
        const val BASE_URL = "https://www.tdtchannels.com/"
    }
}