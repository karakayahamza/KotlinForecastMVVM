package com.example.kotlinweatherforecast.ui.activity

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.kotlinweatherforecast.databinding.ActivityMainBinding
import com.example.kotlinweatherforecast.network.InternetConnectivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.IOException
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val internetConnectivity = application as InternetConnectivity
        internetConnectivity.initContext(this)
        internetConnectivity.checkAndShowInitialInternetStatus()
        internetConnectivity.startMonitoringConnectivity()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Konum izni yoksa izin istenir
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        } else {
            // İzin verildiyse konumu al
            getLastLocation()
        }
    }

    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
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
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                location?.let {
                    // Konumu al ve şehir adını al
                    val geocoder = Geocoder(this, Locale.getDefault())
                    try {
                        val addresses: MutableList<Address>? =
                            geocoder.getFromLocation(it.latitude, it.longitude, 1)
                        if (addresses != null) {
                            if (addresses.isNotEmpty()) {
                                val cityName = addresses[0].adminArea
                                if (cityName != null && cityName.isNotBlank()) {
                                    // Şehir adını kullanabilirsiniz
                                    // Örnek olarak, şehir adını bir TextView'e yazdırabilirsiniz
                                    println("Şehir: $cityName")
                                }
                            }
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Konum izni verildiyse konumu al
                getLastLocation()
            }
        }
    }
}