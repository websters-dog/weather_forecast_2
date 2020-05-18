package io.github.websters_dog.weather_forecast_2.weather

import io.github.websters_dog.weather_forecast_2.location.Coords
import io.reactivex.Observable
import io.reactivex.ObservableSource
import kotlin.reflect.KFunction2


class WeatherRepository private constructor(
    private val cashingWeatherSource: CachingWeatherSource,
    private val basicSources: List<WeatherSource>
) : CachingWeatherSource {

    override fun getForecast(coords: Coords): Observable<Forecast> {
        return getData(
            if (basicSources.isNotEmpty()) basicSources[0]
            else cashingWeatherSource,
            coords,
            WeatherSource::getForecast
        )
    }

    override fun getCurrentWeather(coords: Coords): Observable<CurrentWeather> {
        return getData(
            if (basicSources.isNotEmpty()) basicSources[0]
            else cashingWeatherSource,
            coords,
            WeatherSource::getCurrentWeather
        )
    }

    private fun <T> getData(
        source: WeatherSource,
        coords: Coords,
        function: KFunction2<WeatherSource, Coords, Observable<T>>
    ): Observable<T> {

        var resumeNext: ((Throwable) -> ObservableSource<T>)? = null
        when (source) {
            cashingWeatherSource ->  {
                if (basicSources.isNotEmpty())
                    resumeNext = {
                        getData(basicSources[basicSources.indexOf(source) + 1], coords, function)
                    }
            }
            basicSources.last() -> {
                resumeNext = {
                    getData(basicSources[basicSources.indexOf(source) + 1], coords, function)
                }
            }
        }
        return if (resumeNext != null) function.invoke(source, coords).onErrorResumeNext(resumeNext)
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