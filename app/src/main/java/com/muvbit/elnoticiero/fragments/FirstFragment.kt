package com.muvbit.elnoticiero.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.muvbit.elnoticiero.R
import com.muvbit.elnoticiero.activities.MainActivity
import com.muvbit.elnoticiero.databinding.FragmentFirstBinding
import com.muvbit.elnoticiero.network.weather.ElTiempoApiService
import com.muvbit.elnoticiero.network.weather.Municipio
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
        private val retrofit = Retrofit.Builder()
            .baseUrl("https://www.el-tiempo.net/api/json/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        private val service = retrofit.create(ElTiempoApiService::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activityMainBinding = (activity as MainActivity).binding
        activityMainBinding.bottomNav.menu.clear()

        binding.cvNews.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_firstFragment_to_mainFragment)
        )

        checkLocationPermissionAndFetch()
    }

    private fun checkLocationPermissionAndFetch() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getCurrentLocation()
        } else {
            // Solicitar permisos si no están concedidos
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation()
            } else {
                Log.e("FirstFragment", "Permiso de ubicación denegado.")
                showDefaultLocation()
            }
        }
    }

    private fun getCurrentLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        val locationRequest = CurrentLocationRequest.Builder()
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .setMaxUpdateAgeMillis(0) // Forzar que no use caché
            .build()

        val cancellationToken = CancellationTokenSource()

        if (ActivityCompat.checkSelfPermission(
                this@FirstFragment.requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this@FirstFragment.requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.getCurrentLocation(locationRequest, cancellationToken.token)
            .addOnSuccessListener { location ->
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    Log.d("FirstFragment", "Latitud: $latitude, Longitud: $longitude")

                    // Obtenemos la dirección usando Geocoder en background
                    viewLifecycleOwner.lifecycleScope.launch {
                        getAddressFromLocation(latitude, longitude)
                    }
                } else {
                    Log.e("FirstFragment", "Ubicación es null.")
                    showDefaultLocation()
                }
            }
            .addOnFailureListener {
                Log.e("FirstFragment", "Error obteniendo ubicación: ${it.message}")
                showDefaultLocation()
            }
    }

    private suspend fun getAddressFromLocation(latitude: Double, longitude: Double) {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses?.isNotEmpty() == true) {
                val address = addresses[0]
                val locality = address.locality ?: ""
                val adminArea = address.adminArea ?: ""
                Log.d("FirstFragment", "Dirección encontrada: $locality, $adminArea")

                val postalCode = address.postalCode ?: ""
                Log.d("FirstFragment", "Código postal: $postalCode")
                val codProv = if (postalCode.length >= 2) postalCode.substring(0, 2) else "46" // Default Valencia

                fetchMunicipios(codProv, locality)
            } else {
                showDefaultLocation()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            showDefaultLocation()
        }
    }

    private suspend fun fetchMunicipios(codProv: String, municipioName: String) {
        try {
            val response = service.getMunicipios(codProv)
            if (response.municipios.isNotEmpty()) {
                Log.d("FirstFragment", "Municipios encontrados: ${response.municipios.size}")
                val municipio = findBestMatchMunicipio(response.municipios, municipioName)

                if (municipio != null) {
                    fetchWeatherData(codProv, municipio.CODIGOINE, municipio.NOMBRE, municipio.NOMBRE_PROVINCIA)
                } else {
                    showDefaultLocation()
                }
            } else {
                showDefaultLocation()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            showDefaultLocation()
        }
    }

    private fun findBestMatchMunicipio(municipios: List<Municipio>, targetName: String): Municipio? {
        val normalizedTarget = normalizeName(targetName)
        return municipios.firstOrNull { normalizeName(it.NOMBRE) == normalizedTarget }
            ?: municipios.firstOrNull { normalizeName(it.NOMBRE).contains(normalizedTarget) }
    }

    private fun normalizeName(name: String): String {
        return java.text.Normalizer.normalize(name, java.text.Normalizer.Form.NFD)
            .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
            .lowercase()
            .replace(" ", "")
            .replace("-", "")
            .replace("'", "")
    }

    private suspend fun fetchWeatherData(codProv: String, codigoine: String, municipio: String, provincia: String) {
        try {
            val id = codigoine.substring(0,5)
            val response = service.getTiempoMunicipio(codProv, id)
            withContext(Dispatchers.Main) {
                updateWeatherCard(
                    municipio = municipio,
                    provincia = provincia,
                    temperatura = response.temperatura_actual,
                    estadoCielo = response.stateSky.description,
                    humedad = response.humedad,
                    temperaturaMinima = response.temperaturas.min,
                    temperaturaMaxima = response.temperaturas.max,
                    viento = response.viento,
                    fecha = response.fecha,
                    amanecer = response.pronostico.hoy.attributes.orto,
                    atardecer = response.pronostico.hoy.attributes.ocaso
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            showDefaultLocation()
        }
    }

    private fun updateWeatherCard(
        municipio: String,
        provincia: String,
        temperatura: String,
        estadoCielo: String,
        humedad: String,
        viento: String,
        fecha: String,
        temperaturaMinima: String,
        temperaturaMaxima: String,
        amanecer:String,
        atardecer:String
    ) {
        binding.run {
            tvLocation.text = "$municipio, $provincia"
            tvCurrentDate.text = fecha
            tvTemperature.text = "$temperatura°C"
            tvWeatherCondition.text = estadoCielo
            tvHumidity.text = "$humedad%"
            tvWindSpeed.text = viento+"km/h"
            tvRisingSun.text = amanecer
            tvSunset.text =atardecer
            tvMinTemp.text = "$temperaturaMinima°C"
            tvMaxTemp.text = "$temperaturaMaxima°C"
        }
        updateWeatherIcon(estadoCielo)
    }

    private fun updateWeatherIcon(weatherCondition: String) {
        val iconRes = when {
            weatherCondition.contains("soleado", ignoreCase = true) -> R.drawable.ic_weather_sunny
            weatherCondition.contains("nublado", ignoreCase = true) -> R.drawable.ic_weather_cloudy
            weatherCondition.contains("lluvia", ignoreCase = true) -> R.drawable.ic_weather_rainy
            else -> R.drawable.ic_weather_sunny
        }
        binding.ivWeatherIcon.setImageResource(iconRes)
    }

    private fun showDefaultLocation() {
        updateWeatherCard(
            municipio = "Xativa",
            provincia = "Valencia",
            temperatura = "--",
            estadoCielo = "Datos no disponibles",
            humedad = "--",
            viento = "--",
            fecha = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date()),
            temperaturaMinima = "--",
            temperaturaMaxima = "--",
            amanecer = "--",
            atardecer = "--"
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
