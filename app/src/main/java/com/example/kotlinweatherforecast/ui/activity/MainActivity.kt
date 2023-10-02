package com.example.kotlinweatherforecast.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.kotlinweatherforecast.databinding.ActivityMainBinding
import com.example.kotlinweatherforecast.network.InternetConnectivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val internetConnectivity = application as InternetConnectivity
        internetConnectivity.initContext(this)
        internetConnectivity.checkAndShowInitialInternetStatus()
        internetConnectivity.startMonitoringConnectivity()
    }
}