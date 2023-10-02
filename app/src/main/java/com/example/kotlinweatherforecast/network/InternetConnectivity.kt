package com.example.kotlinweatherforecast.network

import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import kotlin.system.exitProcess

class InternetConnectivity : Application() {

    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback
    private lateinit var mainHandler: Handler
    private lateinit var context: Context

    fun initContext(context: Context) {
        this.context = context
    }


    override fun onCreate() {
        super.onCreate()

        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {

            }

            override fun onLost(network: Network) {

                mainHandler.post {
                    Toast.makeText(applicationContext, "İnternet bağlantısı kaybedildi", Toast.LENGTH_SHORT).show()

                    showAlertDialog()
                }
            }
        }

        mainHandler = Handler(Looper.getMainLooper())
    }

    override fun onTerminate() {
        super.onTerminate()


        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    fun startMonitoringConnectivity() {

        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()


        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    private fun showAlertDialog() {
        val alertDialog = AlertDialog.Builder(context)
            .setTitle("İnternet Bağlantısı Yok")
            .setMessage("Lütfen internet bağlantınızı kontrol edin ve tekrar deneyin.")
            .setPositiveButton("Kapat") { _, _ ->

                exitApplication()
            }
            .setCancelable(false)
            .create()

        alertDialog.show()
    }

    private fun exitApplication() {
        android.os.Process.killProcess(android.os.Process.myPid())
        exitProcess(1)
    }

    fun checkAndShowInitialInternetStatus() {
        if (!isInternetConnected()) {
            showAlertDialog()
        }
    }

    private fun isInternetConnected(): Boolean {
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}