package com.example.kotlinweatherforecast.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
@Entity(tableName = "Places")
class WeatherModel(uuid: Int, cod: String, message: Int, cnt: Int, weatherList: List<list>, city: City) {
    @PrimaryKey(autoGenerate = true)
    var uuid = 0

    @ColumnInfo(name = "cod")
    @SerializedName("cod")
    var cod: String

    @ColumnInfo(name = "message")
    @SerializedName("message")
    var message: Int

    @ColumnInfo(name = "cnt")
    @SerializedName("cnt")
    var cnt: Int

    @SerializedName("list")
    @ColumnInfo(name = "list")
    var weatherList: List<list>

    @ColumnInfo(name = "city")
    @SerializedName("city")
    var city: City

    inner class Main {
        @ColumnInfo(name = "temp")
        @SerializedName("temp")
        var temp = 0.0

        @ColumnInfo(name = "feels_like")
        @SerializedName("feels_like")
        var feels_like = 0.0

        @ColumnInfo(name = "temp_min")
        @SerializedName("temp_min")
        var temp_min = 0.0

        @ColumnInfo(name = "temp_max")
        @SerializedName("temp_max")
        var temp_max = 0.0

        @ColumnInfo(name = "pressure")
        @SerializedName("pressure")
        var pressure = 0

        @ColumnInfo(name = "sea_level")
        @SerializedName("sea_level")
        var sea_level = 0

        @ColumnInfo(name = "humidity")
        @SerializedName("humidity")
        var humidity = 0

        @ColumnInfo(name = "temp_kf")
        @SerializedName("temp_kf")
        var temp_kf = 0.0
    }

    inner class Weather {
        @ColumnInfo(name = "id")
        @SerializedName("id")
        var id = 0

        @ColumnInfo(name = "main")
        @SerializedName("main")
        var main: String? = null

        @ColumnInfo(name = "description")
        @SerializedName("description")
        var description: String? = null

        @ColumnInfo(name = "icon")
        @SerializedName("icon")
        var icon: String? = null
    }

    inner class Clouds {
        @ColumnInfo(name = "all")
        @SerializedName("all")
        var all = 0
    }

    inner class Wind {
        @ColumnInfo(name = "speed")
        @SerializedName("speed")
        var speed = 0.0

        @ColumnInfo(name = "deg")
        @SerializedName("deg")
        var deg = 0

        @ColumnInfo(name = "gust")
        @SerializedName("gust")
        var gust = 0.0
    }

    inner class Sys {
        @ColumnInfo(name = "pod")
        @SerializedName("pod")
        var pod: String? = null
    }

    inner class Rain {
        @ColumnInfo(name = "_3h")
        @SerializedName("3h")
        var _3h = 0.0
    }

    inner class list {
        @ColumnInfo(name = "dt")
        @SerializedName("dt")
        var dt = 0

        @ColumnInfo(name = "main")
        @SerializedName("main")
        var main: Main? = null

        @ColumnInfo(name = "weather")
        @SerializedName("weather")
        var weather: List<Weather>? = null

        @ColumnInfo(name = "clouds")
        @SerializedName("clouds")
        var clouds: Clouds? = null

        @ColumnInfo(name = "wind")
        @SerializedName("wind")
        var wind: Wind? = null

        @ColumnInfo(name = "visibility")
        @SerializedName("visibility")
        var visibility = 0

        @ColumnInfo(name = "pop")
        @SerializedName("pop")
        var pop = 0.0

        @ColumnInfo(name = "sys")
        @SerializedName("sys")
        var sys: Sys? = null

        @ColumnInfo(name = "dt_txt")
        @SerializedName("dt_txt")
        var dt_txt: String? = null

        @ColumnInfo(name = "rain")
        @SerializedName("rain")
        var rain: Rain? = null
    }

    inner class Coord {
        @ColumnInfo(name = "lon")
        @SerializedName("lon")
        var lon = 0.0

        @ColumnInfo(name = "lat")
        @SerializedName("lat")
        var lat = 0.0
    }

    inner class City {
        @ColumnInfo(name = "id")
        @SerializedName("id")
        var id = 0

        @ColumnInfo(name = "name")
        @SerializedName("name")
        var name: String? = null

        @ColumnInfo(name = "coord")
        @SerializedName("coord")
        var coord: Coord? = null

        @ColumnInfo(name = "country")
        @SerializedName("country")
        var country: String? = null

        @ColumnInfo(name = "population")
        @SerializedName("population")
        var population = 0

        @ColumnInfo(name = "timezone")
        @SerializedName("timezone")
        var timezone = 0

        @ColumnInfo(name = "sunrise")
        @SerializedName("sunrise")
        var sunrise = 0

        @ColumnInfo(name = "sunset")
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