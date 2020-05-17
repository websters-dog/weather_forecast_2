package io.github.websters_dog.weather_forecast_2.weather

import android.content.Context
import io.github.websters_dog.weather_forecast_2.location.LocationRepository
import io.reactivex.Observable
import io.reactivex.ObservableSource
import kotlin.reflect.KFunction1


class WeatherRepository private constructor(
    private val cashingWeatherSource: CachingWeatherSource,
    private val basicSources: List<WeatherSource>
) : CachingWeatherSource {

    override fun getForecast(): Observable<Forecast> {
        return getData(
            if (basicSources.isNotEmpty()) basicSources[0]
            else cashingWeatherSource, WeatherSource::getForecast
        )
    }

    override fun getCurrentWeather(): Observable<CurrentWeather> {
        return getData(
            if (basicSources.isNotEmpty()) basicSources[0]
            else cashingWeatherSource, WeatherSource::getCurrentWeather
        )
    }

    private fun <T> getData(
        source: WeatherSource,
        function: KFunction1<WeatherSource, Observable<T>>): Observable<T> {
        var resumeNext: ((Throwable) -> ObservableSource<T>)? = null
        when (basicSources.indexOf(source)) {
            in 0..basicSources.size - 2 -> {
                resumeNext = { getData(basicSources[basicSources.indexOf(source) + 1], function) }
            }
            basicSources.size - 1 -> {
                resumeNext = { getData(cashingWeatherSource, function) }
            }
        }
        return if (resumeNext != null) function.invoke(source).onErrorResumeNext(resumeNext)
            else function.call(source)
    }

    override fun cacheForecast(forecast: Forecast) {
        cashingWeatherSource.cacheForecast(forecast)
    }

    override fun cacheCurrentWeather(currentWeather: CurrentWeather) {
        cashingWeatherSource.cacheCurrentWeather(currentWeather)
    }

    override fun invalidateCache() {
        cashingWeatherSource.invalidateCache()
    }


    companion object {
        fun getInstance(): WeatherRepository {
            return WeatherRepository(
                RealmWeatherSource(),
                listOf(RestWeatherSource())
            )
        }
    }

}