package com.example.kotlinforecast.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.kotlinforecast.databinding.ActivityMainBinding
import com.example.kotlinforecast.network.InternetConnectivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val internetConnectivity = application as InternetConnectivity
        internetConnectivity.initContext(this)
        internetConnectivity.startMonitoringConnectivity()
    }
}