package com.example.kotlinforecast.data.weatherRepository

import com.example.kotlinforecast.data.api.WeatherApi
import com.example.kotlinforecast.data.model.WeatherModel
import io.reactivex.Observable

class WeatherRepository(private val weatherApi: WeatherApi) {

    fun getData(name: String, appId: String): Observable<WeatherModel> {
        return weatherApi.getData(name, appId, "metric")
    }
}
