package com.example.kotlinforecast.data.api

import com.example.kotlinforecast.data.model.WeatherModel
import retrofit2.http.GET
import retrofit2.http.Query
    interface WeatherApi {
        @GET("forecast?")
        fun getData(
            @Query("q") name: String?,
            @Query("APPID") appId: String?,
            @Query("units") units : String?
        ): io.reactivex.Observable<WeatherModel>
    }