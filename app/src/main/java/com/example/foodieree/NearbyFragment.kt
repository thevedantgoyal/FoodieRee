package com.example.foodieree

import android.content.pm.PackageManager
import android.location.Location
import android.Manifest
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat
import com.example.foodieree.model.FoodSpot
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class NearbyFragment : Fragment(), OnMapReadyCallback {
    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var userLocation: LatLng? = null



    private val foodSpots = listOf(
        FoodSpot("Burger Point", 28.6500, 77.3800),
        FoodSpot("Pizza Delight", 28.6550, 77.3850),
        FoodSpot("Tandoori Junction", 28.6600, 77.3900),
        FoodSpot("Street Snacks", 28.6650, 77.3950),
        FoodSpot("Dessert Haven", 28.6700, 77.4000)
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_nearby, container, false)

        mapView = view.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())

        return view
    }
    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap.uiSettings.isZoomControlsEnabled = true

        getUserLocation()
    }

    private fun getUserLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
            return
        }

        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                userLocation = LatLng(location.latitude, location.longitude)
                googleMap.addMarker(MarkerOptions().position(userLocation!!).title("You are here"))
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation!!, 12f))

                addNearbyFoodSpots()
            }
        }
    }
    private fun addNearbyFoodSpots() {
        userLocation?.let { userLatLng ->
            val filteredSpots = foodSpots.filter { isWithin10Km(it, userLatLng) }

            for (spot in filteredSpots) {
                val latLng = LatLng(spot.lat, spot.lon)
                googleMap.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .title("${spot.name} - ${calculateDistance(userLatLng, latLng)} km away")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                )
            }
        }
    }
    private fun isWithin10Km(spot: FoodSpot, userLatLng: LatLng): Boolean {
        val distance = calculateDistance(userLatLng, LatLng(spot.lat, spot.lon))
        return distance <= 10
    }

    private fun calculateDistance(start: LatLng, end: LatLng): Double {
        val results = FloatArray(1)
        Location.distanceBetween(start.latitude, start.longitude, end.latitude, end.longitude, results)

        return (results[0] / 1000).toDouble()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }
}