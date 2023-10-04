package com.example.kotlinweatherforecast.utils

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.IOException
import java.util.Locale

class LocationHelper(private val activity: Activity) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(activity)

    fun getLastLocationWithPermission(
        onLocationReceived: (Location?, String?) -> Unit
    ) {
        if (ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        } else {
            getLastLocation(onLocationReceived)
        }
    }

    private fun getLastLocation(onLocationReceived: (Location?, String?) -> Unit) {
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                location?.let {
                    val geocoder = Geocoder(activity, Locale.getDefault())
                    try {
                        val addresses: MutableList<Address>? =
                            geocoder.getFromLocation(it.latitude, it.longitude, 1)
                        if (addresses != null) {
                            if (addresses.isNotEmpty()) {
                                val cityName = addresses[0].adminArea
                                onLocationReceived(location, cityName)
                            } else {
                                onLocationReceived(location, null)
                            }
                        } else {
                            onLocationReceived(location, null)
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                        onLocationReceived(location, null)
                        println(e)
                    }
                }
            }
    }
}