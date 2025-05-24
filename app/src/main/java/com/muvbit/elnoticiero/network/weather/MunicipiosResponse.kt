package com.muvbit.elnoticiero.network.weather

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Path

// Servicio de la API
interface ElTiempoApiService {
    @GET("provincias/{codProv}/municipios")
    suspend fun getMunicipios(@Path("codProv") codProv: String): MunicipiosResponse

    @GET("provincias/{codProv}/municipios/{id}")
    suspend fun getTiempoMunicipio(
        @Path("codProv") codProv: String,
        @Path("id") id: String
    ): TiempoResponse
}

// Objetos de respuesta
data class MunicipiosResponse(
    val municipios: List<Municipio>
)

data class Municipio(
    val CODIGOINE: String,
    val NOMBRE: String,
    val NOMBRE_PROVINCIA: String
)

data class TiempoResponse(
    val temperatura_actual: String,
    val stateSky: StateSky,
    val humedad: String,
    val viento: String,
    val fecha: String,
    val temperaturas: Temperaturas,
    val pronostico: Pronostico
)

data class StateSky(
    val description: String
)

data class Temperaturas(
    val min: String,
    val max: String
)

data class Pronostico(
    val hoy: Hoy
)

data class Hoy(
    @SerializedName("@attributes")
    val attributes: HoyAttributes
)

data class HoyAttributes(
    val fecha: String,
    val orto: String,
    val ocaso: String
)