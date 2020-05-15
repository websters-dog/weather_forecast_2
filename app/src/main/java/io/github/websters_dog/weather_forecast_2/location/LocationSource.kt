package io.github.websters_dog.weather_forecast_2.location

import io.reactivex.rxjava3.core.Single

interface LocationSource {
    fun getLocation(): Single<Coords>
}


interface CachingLocationSource : LocationSource {
    fun cacheLocation(coords: Coords)
    fun invalidateCache()
}


class NoLocationFoundException : Exception("No cache found")