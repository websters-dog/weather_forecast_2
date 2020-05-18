package io.github.websters_dog.weather_forecast_2.weather

import io.github.websters_dog.weather_forecast_2.location.Coords
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class RestWeatherSource : WeatherSource {

    private val mapApiService = OpenWeatherMapApiService.create()

    override fun getForecast(coords: Coords): Observable<Forecast> {
        return mapApiService
            .LoadForecast(coords.latitude, coords.longitude)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
    }

    override fun getCurrentWeather(coords: Coords): Observable<CurrentWeather> {
        return mapApiService
            .LoadCurrentWeather(coords.latitude, coords.longitude)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
    }

}