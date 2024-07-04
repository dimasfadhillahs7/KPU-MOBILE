package com.dimasfs.kpu.fragment

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.dimasfs.kpu.database.PemilihHelper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale


class MapsFragment : Fragment(com.dimasfs.kpu.R.layout.fragment_maps), OnMapReadyCallback {
    private lateinit var dbHelper: PemilihHelper
    private lateinit var fusedLocation: FusedLocationProviderClient

    private val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    private val REQUEST_CODE_PERMISSIONS = 100
    private val DEFAULT_ZOOM = 15f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHelper = PemilihHelper(requireContext())
    }

    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
        val mapFragment = childFragmentManager.findFragmentById(com.dimasfs.kpu.R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireContext(),
            it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getDeviceLocation(googleMap: GoogleMap) {
        fusedLocation = LocationServices.getFusedLocationProviderClient(requireContext())
        try {
            if (allPermissionsGranted()) {
                val locationTask = fusedLocation.lastLocation
                locationTask.addOnSuccessListener {
                        if (it!=null) {
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(
                                it.latitude,
                                it.longitude
                            ), DEFAULT_ZOOM))

                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Lokasi Tidak Ditemukan!, Aktifkan Lokasi Anda",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                }
            }
        } catch (e: SecurityException) {

        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        getDeviceLocation(googleMap)
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        googleMap.isMyLocationEnabled = true
        googleMap.uiSettings.isMyLocationButtonEnabled = true

        lifecycleScope.launch {
            val markerDataList = getMarkerData()
            for (markerData in markerDataList) {
                val position = LatLng(markerData.latitude, markerData.longitude)
                googleMap.addMarker(MarkerOptions().position(position).title("Nama : "+markerData.name).snippet("NIK : "+markerData.nik))
            }

            if (markerDataList.isNotEmpty()) {
                val firstMarkerPosition = LatLng(markerDataList[0].latitude, markerDataList[0].longitude)
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstMarkerPosition, DEFAULT_ZOOM))
            }
        }

        googleMap.setOnInfoWindowClickListener { marker ->
            val nik = marker.snippet?.split("NIK : ")?.lastOrNull()
            if (nik != null) {
                DetailFragment.newInstance(nik).show(parentFragmentManager, "DetailFragment")
            }
            true
        }
    }

    private suspend fun getMarkerData(): List<MarkerData> {
        return withContext(Dispatchers.IO) {
            val markerDataList = mutableListOf<MarkerData>()
            val pemilihList = dbHelper.getPemilih()
            for (pemilih in pemilihList) {
                val latitude = pemilih.latitude
                val longitude = pemilih.longitude

                if (latitude != null && longitude != null) {
                    markerDataList.add(MarkerData(pemilih.nama, pemilih.nik, latitude, longitude))
                }
            }
            markerDataList
        }
    }



    data class MarkerData(val name: String, val nik: String, val latitude: Double, val longitude: Double)
}