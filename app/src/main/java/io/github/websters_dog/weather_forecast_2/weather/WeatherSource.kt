package io.github.websters_dog.weather_forecast_2.weather

import io.reactivex.Observable


interface WeatherSource {
    fun getForecast(): Observable<Forecast>
    fun getCurrentWeather(): Observable<CurrentWeather>
}


interface CachingWeatherSource : WeatherSource {
    fun cacheForecast(forecast: Forecast)
    fun cacheCurrentWeather(currentWeather: CurrentWeather)
    fun invalidateCache()
}


class NoWeatherCacheException : Exception("No weather cache found")