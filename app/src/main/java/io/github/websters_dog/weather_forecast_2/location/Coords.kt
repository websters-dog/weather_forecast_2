package io.github.websters_dog.weather_forecast_2.location

import kotlin.math.pow
import kotlin.math.sqrt

infix fun Coords?.distanceTo(coords2: Coords): Float {
    //todo replace with geographical check
    return this?.let {
        sqrt((it.latitude - coords2.latitude).pow(2) + (it.longitude - coords2.longitude).pow(2))
    }
        ?: Float.MAX_VALUE
}

data class Coords(
    val latitude: Float,
    val longitude: Float
)