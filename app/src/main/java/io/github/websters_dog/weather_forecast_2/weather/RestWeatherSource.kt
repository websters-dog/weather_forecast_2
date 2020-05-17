package io.github.websters_dog.weather_forecast_2.weather

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class RestWeatherSource : WeatherSource {

    private val mapApiService = OpenWeatherMapApiService.create()

    override fun getForecast(): Observable<Forecast> {
        return mapApiService
            .LoadForecast(54f, 27f)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
    }

    override fun getCurrentWeather(): Observable<CurrentWeather> {
        return mapApiService
            .LoadCurrentWeather(54f, 27f)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
    }

}