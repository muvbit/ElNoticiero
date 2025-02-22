package com.muvbit.elnoticiero.fragments

import android.Manifest
import android.app.DatePickerDialog
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.DialogFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.muvbit.elnoticiero.databinding.FragmentFilterDialogBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class FilterDialogFragment : DialogFragment(), OnMapReadyCallback {

    private var _binding: FragmentFilterDialogBinding? = null
    private val binding get() = _binding!!

    private var listener: FilterDialogListener? = null
    private val calendar = Calendar.getInstance()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var selectedLocation: LatLng? = null
    private var googleMap: GoogleMap? = null
    private var currentMarker: Marker? = null
    private var radius: Int = 10
    private var useLocation: Boolean = true

    interface FilterDialogListener {
        fun onFiltersApplied(
            startDate: String?,
            endDate: String?,
            newsNumber: String?,
            country: String?,
            language: String?,
            coordinates: String?
        )
        fun onDialogDismissed()
    }

    fun setListener(listener: FilterDialogListener) {
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFilterDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        val mapFragment = childFragmentManager.findFragmentById(binding.mapFragment.id) as SupportMapFragment
        mapFragment.getMapAsync(this)

        setupDatePicker(binding.etStartDate)
        setupDatePicker(binding.etEndDate)
        setupSeekBar(binding.sbRadius, binding.tvRadius)
        setupCheckBox(binding.cbUseLocation)

        binding.btnGetCurrentLocation.setOnClickListener {
            getCurrentLocation()
        }

        binding.btnApplyFilters.setOnClickListener {
            val startDate = binding.etStartDate.text.toString().takeIf { it.isNotEmpty() }
            val endDate = binding.etEndDate.text.toString().takeIf { it.isNotEmpty() }
            val newsNumber = binding.etNewsNumber.text.toString().takeIf { it.isNotEmpty() }
            val country = binding.etCountry.text.toString().takeIf { it.isNotEmpty() }
            val language = binding.etLanguage.text.toString().takeIf { it.isNotEmpty() }
            val coordinates = if (useLocation && selectedLocation != null) {
                "${selectedLocation!!.latitude},${selectedLocation!!.longitude},$radius"
            } else null

            listener?.onFiltersApplied(startDate, endDate, newsNumber, country, language, coordinates)
            dismiss()
        }
    }

    private fun setupDatePicker(editText: EditText) {
        editText.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    editText.setText(dateFormat.format(calendar.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }
    }

    private fun setupSeekBar(seekBar: SeekBar, textView: TextView) {
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                radius = if (progress < 10) 10 else progress
                textView.text = "Radio: $radius km"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun setupCheckBox(checkBox: CheckBox) {
        checkBox.setOnCheckedChangeListener { _, isChecked ->
            useLocation = isChecked
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    val currentLatLng = LatLng(it.latitude, it.longitude)
                    updateMapLocation(currentLatLng, "Mi ubicación")
                }
            }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    // Precise location access granted.
                    getCurrentLocation()
                }

                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    // Only approximate location access granted.
                    getCurrentLocation()
                }

                else -> {
                    // No location access granted.
                    Log.e("FilterDialogFragment", "No location access granted")
                }
            }
        }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        val valencia = LatLng(39.4699, -0.3763) // Coordenadas de Valencia
        updateMapLocation(valencia, "Valencia")

        googleMap?.setOnMapClickListener { latLng ->
            updateMapLocation(latLng, "Ubicación seleccionada")
        }
    }

    private fun updateMapLocation(latLng: LatLng, title: String) {
        selectedLocation = latLng
        currentMarker?.remove()
        currentMarker = googleMap?.addMarker(MarkerOptions().position(latLng).title(title))
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDismiss(dialog: android.content.DialogInterface) {
        super.onDismiss(dialog)
        listener?.onDialogDismissed()
    }
}