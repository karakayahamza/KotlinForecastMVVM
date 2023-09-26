package com.example.kotlinweatherforecast.data.weatherRepository

import com.example.kotlinweatherforecast.data.api.WeatherApi
import com.example.kotlinweatherforecast.data.model.WeatherModel
import io.reactivex.Observable

class WeatherRepository(private val weatherApi: WeatherApi) {
    fun getData(name: String, appId: String): Observable<WeatherModel> {
        return weatherApi.getData(name, appId, "metric")
    }
}
