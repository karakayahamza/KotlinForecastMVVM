package com.example.kotlinweatherforecast.data.api

import com.example.kotlinweatherforecast.data.model.WeatherModel
import retrofit2.http.GET
import retrofit2.http.Query
    interface WeatherApi {
        @GET("forecast?&lang=tr")
        fun getData(
            @Query("q") name: String?,
            @Query("APPID") appId: String?,
            @Query("units") units : String?
        ): io.reactivex.Observable<WeatherModel>
    }