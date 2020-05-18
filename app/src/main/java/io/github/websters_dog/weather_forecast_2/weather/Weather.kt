package io.github.websters_dog.weather_forecast_2.weather

import io.github.websters_dog.weather_forecast_2.location.Coords
import io.realm.RealmList
import io.realm.RealmObject


open class Forecast(
    var list: RealmList<ForecastItem>? = null,
    var city: City? = null
) : RealmObject(), DataInfo {
    override fun toString(): String {
        return "Forecast: weather=${list?.joinToString()}, city=$city"
    }

    override fun getCoords(): Coords? {
        return city?.coord?.run { Coords(this.lat, this.lon) }
    }

    override fun getMillisDt(): Long {
        return list?.first()?.getMillisDt() ?: 0
    }
}


open class CurrentWeather(
    var dt: Long = 0,
    var main: MainWeatherInfoBlock? = null,
    var weather: RealmList<WeatherItem>? = null,
    var wind: Wind? = null,
    var sys: Sys? = null,
    var precipitation: Precipitation? = null,
    var coord: WeatherCoord? = null,
    var name: String = ""
) : RealmObject(), DataInfo {
    override fun toString(): String {
        return "CurrentWeather: " +
                "dt=$dt, " +
                "main=$main, " +
                "weather=${weather?.joinToString()}" +
                "super=${super.toString()}, " +
                "wind=$wind, " +
                "sys=${sys}, " +
                "precipitation=${precipitation}, " +
                "coord=${coord}, " +
                "name=${name}"
    }

    override fun getCoords(): Coords? {
        return coord?.run { Coords(this.lat, this.lon) }
    }

    override fun getMillisDt(): Long {
        return dt * 1000
    }
}


open class ForecastItem(
    var dt: Long = 0,
    var main: MainWeatherInfoBlock? = null,
    var weather: RealmList<WeatherItem>? = null
) : RealmObject() {
    override fun toString(): String {
        return "WeatherInfo: dt=$dt, main=$main, weather=${weather?.joinToString()}"
    }

    fun getMillisDt(): Long {
        return dt * 1000
    }
}


open class MainWeatherInfoBlock(
    var temp: Float = 0f,
    var pressure: Float = 0f,
    var humidity: Float = 0f
) : RealmObject() {
    override fun toString(): String {
        return "MainWeatherInfoBlock: temp=$temp, pressure=$pressure, humidity=$humidity"
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
    var coord: WeatherCoord? = null,
    var country: String = ""
) : RealmObject() {
    override fun toString(): String {
        return "City: id=$id, name=$name, coord=$coord, country=$country"
    }
}


open class WeatherCoord(
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


interface DataInfo {
    fun getCoords(): Coords?
    fun getMillisDt(): Long
}