package io.github.websters_dog.weather_forecast_2.weather

import io.realm.RealmList
import io.realm.RealmObject


open class Forecast(
    var list: RealmList<ForecastItem>? = null,
    var city: City? = null
) : RealmObject() {
    override fun toString(): String {
        return "Forecast: weather=${list?.joinToString()}, city=$city"
    }
}


open class CurrentWeather(
    var dt: Long = 0,
    var mainBlock: MainWeatherInfoBlock? = null,
    var weatherItem: RealmList<WeatherItem>? = null,
    var wind: Wind? = null,
    var sys: Sys? = null,
    var precipitation: Precipitation? = null,
    var coord: Coord? = null,
    var name: String = ""
) : RealmObject() {
    override fun toString(): String {
        return "CurrentWeather: " +
                "dt=$dt, " +
                "main=$mainBlock, " +
                "weather=${weatherItem?.joinToString()}" +
                "super=${super.toString()}, " +
                "wind=$wind, " +
                "sys=${sys}, " +
                "precipitation=${precipitation}, " +
                "coord=${coord}, " +
                "name=${name}"
    }
}


open class ForecastItem(
    var dt: Long = 0,
    var mainBlock: MainWeatherInfoBlock? = null,
    var weatherItem: RealmList<WeatherItem>? = null
) : RealmObject() {
    override fun toString(): String {
        return "WeatherInfo: dt=$dt, main=$mainBlock, weather=${weatherItem?.joinToString()}"
    }
}


open class MainWeatherInfoBlock(
    var temp: Float = 0f,
    var pressure: Float = 0f
) : RealmObject() {
    override fun toString(): String {
        return "MainWeatherInfoBlock: temp=$temp, pressure=$pressure"
    }
}


open class WeatherItem(
    var id: Long = 0,
    var main: String = "",
    var description: String = "",
    var icon: String = ""
) : RealmObject() {
    override fun toString(): String {
        return "WeatherItem: id=$id, main=$main, description=$description, icon=$icon"
    }
}


open class Wind(
    var speed: Float = 0f,
    var deg: Float = 0f
) : RealmObject() {
    override fun toString(): String {
        return "Wind: speed=$speed, deg=$deg"
    }
}


open class Precipitation(
    var value: Float = 0f,
    var mode: Float = 0f
) : RealmObject() {
    override fun toString(): String {
        return "Precipitation: value=$value, mode=$mode"
    }
}


open class City(
    var id: Long = 0,
    var name: String = "",
    var coord: Coord? = null,
    var country: String = ""
) : RealmObject() {
    override fun toString(): String {
        return "City: id=$id, name=$name, coord=$coord, country=$country"
    }
}


open class Coord(
    var lat: Float = 0f,
    var lon: Float = 0f
) : RealmObject() {
    override fun toString(): String {
        return "Coord: lat=$lat, lon=$lon"
    }
}


open class Sys(
    var country: String = ""
) : RealmObject() {
    override fun toString(): String {
        return "Sys: country=$country"
    }
}