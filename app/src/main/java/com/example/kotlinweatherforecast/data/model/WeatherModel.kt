package com.example.kotlinweatherforecast.data.model

import com.google.gson.annotations.SerializedName

class WeatherModel(uuid: Int, cod: String, message: Int, cnt: Int, weatherList: List<list>, city: City) {
    @SerializedName("uuid")
    var uuid = 0

    @SerializedName("cod")
    var cod: String
    
    @SerializedName("message")
    var message: Int

    @SerializedName("cnt")
    var cnt: Int

    @ColumnInfo(name = "list")
    var weatherList: List<list>

    @SerializedName("city")
    var city: City

    inner class Main {
        @SerializedName("temp")
        var temp = 0.0

        @SerializedName("feels_like")
        var feels_like = 0.0

        @SerializedName("temp_min")
        var temp_min = 0.0

        @SerializedName("temp_max")
        var temp_max = 0.0

        @SerializedName("pressure")
        var pressure = 0

        @SerializedName("sea_level")
        var sea_level = 0

        @SerializedName("humidity")
        var humidity = 0

        @SerializedName("temp_kf")
        var temp_kf = 0.0
    }

    inner class Weather {
        @SerializedName("id")
        var id = 0

        @SerializedName("main")
        var main: String? = null

        @SerializedName("description")
        var description: String? = null

        @SerializedName("icon")
        var icon: String? = null
    }

    inner class Clouds {
        @SerializedName("all")
        var all = 0
    }

    inner class Wind {
        @SerializedName("speed")
        var speed = 0.0

        @SerializedName("deg")
        var deg = 0

        @SerializedName("gust")
        var gust = 0.0
    }

    inner class Sys {
        @SerializedName("pod")
        var pod: String? = null
    }

    inner class Rain {
        @SerializedName("3h")
        var _3h = 0.0
    }

    inner class list {
        @SerializedName("dt")
        var dt = 0

        @SerializedName("main")
        var main: Main? = null

        @SerializedName("weather")
        var weather: List<Weather>? = null

        @SerializedName("clouds")
        var clouds: Clouds? = null

        @SerializedName("wind")
        var wind: Wind? = null

        @SerializedName("visibility")
        var visibility = 0

        @SerializedName("pop")
        var pop = 0.0

        @SerializedName("sys")
        var sys: Sys? = null

        @SerializedName("dt_txt")
        var dt_txt: String? = null

        @SerializedName("rain")
        var rain: Rain? = null
    }

    inner class Coord {
        @SerializedName("lon")
        var lon = 0.0

        @SerializedName("lat")
        var lat = 0.0
    }

    inner class City {
        @SerializedName("id")
        var id = 0

        @SerializedName("name")
        var name: String? = null

        @SerializedName("coord")
        var coord: Coord? = null

        @SerializedName("country")
        var country: String? = null

        @SerializedName("population")
        var population = 0

        @SerializedName("timezone")
        var timezone = 0

        @SerializedName("sunrise")
        var sunrise = 0

        @SerializedName("sunset")
        var sunset = 0
    }

    init {
        this.uuid = uuid
        this.cod = cod
        this.message = message
        this.cnt = cnt
        this.weatherList = weatherList
        this.city = city
    }
}
