package io.github.websters_dog.weather_forecast_2.view

import io.github.websters_dog.weather_forecast_2.R
import io.github.websters_dog.weather_forecast_2.weather.WeatherItem
import java.lang.IllegalArgumentException

fun getWeatherIconUrl(weather: WeatherItem?): String {
    return "https://openweathermap.org/img/wn/${weather?.icon ?: "01d"}@2x.png"
}

fun getWindDirectionTextRes(degrees: Float): Int {

    return when (degrees.coerceIn(0f, 360f)) {
        in 348.75f..360f,
        in 0f..11.25f -> R.string.current_weather_wind_direction_n
        in 11.25..33.75 -> R.string.current_weather_wind_direction_nne
        in 33.75.. 56.25 -> R.string.current_weather_wind_direction_ne
        in 56.25..78.75 -> R.string.current_weather_wind_direction_ene
        in 78.75.. 101.25 -> R.string.current_weather_wind_direction_e
        in 101.25..123.75 -> R.string.current_weather_wind_direction_ese
        in 123.75.. 146.25 -> R.string.current_weather_wind_direction_se
        in 146.25..168.75 -> R.string.current_weather_wind_direction_sse
        in 168.75.. 191.25 -> R.string.current_weather_wind_direction_s
        in 191.25..213.75 -> R.string.current_weather_wind_direction_ssw
        in 213.75.. 236.25 -> R.string.current_weather_wind_direction_sw
        in 236.25..258.75 -> R.string.current_weather_wind_direction_wsw
        in 258.75.. 281.25 -> R.string.current_weather_wind_direction_w
        in 281.25..303.75 -> R.string.current_weather_wind_direction_wnw
        in 303.75.. 326.25 -> R.string.current_weather_wind_direction_nw
        in 326.25..348.75 -> R.string.current_weather_wind_direction_nnw
        else -> throw IllegalArgumentException()
    }
}