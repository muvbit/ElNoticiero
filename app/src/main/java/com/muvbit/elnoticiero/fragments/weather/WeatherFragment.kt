package com.muvbit.elnoticiero.fragments.weather
import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.muvbit.elnoticiero.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.io.IOException
import java.util.Locale

class WeatherFragment : Fragment() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var apiKey = "TU_API_KEY_DE_ELTIEMPO.NET" // Reemplaza con tu API key

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_weather, container, false)

        // Inicializa el cliente de ubicación
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // Verifica permisos y obtén la ubicación
        getCurrentLocation()

        return view
    }

    private fun getCurrentLocation() {
        // Verificar permisos
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Solicitar permisos si no los tienes
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        // Obtener la última ubicación conocida
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    // Convertir coordenadas a municipio
                    getCityName(location.latitude, location.longitude)
                } else {
                    Log.e("WeatherFragment", "Ubicación no disponible")
                    // Puedes mostrar una ubicación por defecto o un mensaje de error
                }
            }
            .addOnFailureListener { e ->
                Log.e("WeatherFragment", "Error al obtener ubicación: ${e.message}")
            }
    }

    private fun getCityName(latitude: Double, longitude: Double) {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        try {
            val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses != null) {
                if (addresses.isNotEmpty()) {
                    val city = addresses[0].locality ?: addresses[0].subAdminArea ?: ""
                    val province = addresses[0].adminArea ?: ""

                    if (city.isNotEmpty()) {
                        // Obtener datos meteorológicos
                        fetchWeatherData(city, province)
                    }
                }
            }
        } catch (e: IOException) {
            Log.e("WeatherFragment", "Error en geocoding: ${e.message}")
        }
    }

    private fun fetchWeatherData(city: String, province: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.el-tiempo.net/api/json/v2/") // Verifica la URL base de la API
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(WeatherApiService::class.java)

        // Ajusta según la estructura de la API eltiempo.net
        val call = service.getWeatherData(apiKey, city, province)

        call.enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                if (response.isSuccessful) {
                    val weatherData = response.body()
                    // Actualizar la UI con los datos del tiempo
                    updateWeatherUI(weatherData)
                } else {
                    Log.e("WeatherFragment", "Error en la respuesta: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                Log.e("WeatherFragment", "Error en la llamada API: ${t.message}")
            }
        })
    }

    private fun updateWeatherUI(weatherData: WeatherResponse?) {
        // Actualiza tu CardView con los datos del tiempo
        // Ejemplo:
        /*
        binding.tvCity.text = weatherData?.city ?: "Desconocido"
        binding.tvTemperature.text = "${weatherData?.temperature ?: "N/A"}°C"
        binding.tvCondition.text = weatherData?.condition ?: "N/A"

        // Si hay icono
        Glide.with(this)
            .load(weatherData?.iconUrl)
            .into(binding.ivWeatherIcon)
        */
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCurrentLocation()
                }
            }
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }
}

// Interfaz para la API
interface WeatherApiService {
    @GET("provincias/{province}/{city}") // Ajusta según la API real
    fun getWeatherData(
        @Query("api_key") apiKey: String,
        @Path("city") city: String,
        @Path("province") province: String
    ): Call<WeatherResponse>
}

// Modelo de datos (ajusta según la respuesta de la API)
data class WeatherResponse(
    val city: String,
    val temperature: Int,
    val condition: String,
    val iconUrl: String?,
    // otros campos que necesites
)