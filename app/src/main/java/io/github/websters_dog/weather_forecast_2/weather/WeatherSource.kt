package io.github.websters_dog.weather_forecast_2.weather

import io.github.websters_dog.weather_forecast_2.location.Coords
import io.reactivex.Observable


interface WeatherSource {
    fun getForecast(coords: Coords): Observable<Forecast>
    fun getCurrentWeather(coords: Coords): Observable<CurrentWeather>
}


interface CachingWeatherSource : WeatherSource {
    fun cacheForecast(forecast: Forecast)
    fun cacheCurrentWeather(currentWeather: CurrentWeather)
    fun invalidateCache()
}


class NoWeatherCacheException : Exception("No weather cache found")