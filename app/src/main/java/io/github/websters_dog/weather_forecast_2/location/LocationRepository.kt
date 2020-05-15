package io.github.websters_dog.weather_forecast_2.location

import android.content.Context
import android.util.Log
import io.reactivex.rxjava3.core.Single


class LocationRepository private constructor(
    private val basicSource: LocationSource,
    private val cachingSource: CachingLocationSource
) : CachingLocationSource {

    init {
        invalidateCache()
    }

    override fun cacheLocation(coords: Coords) {
        cachingSource.cacheLocation(coords)
    }

    override fun invalidateCache() {
        cachingSource.invalidateCache()
    }

    override fun getLocation(): Single<Coords> {
        return cachingSource.getLocation()
            .onErrorResumeNext { throwable ->
                Log.e("[location]", throwable.toString())
                basicSource.getLocation()
                    .doOnSuccess { cachingSource.cacheLocation(it) }
            }
    }


    companion object {
        fun getInstance(context: Context): LocationRepository {
            return LocationRepository(GpsLocationSource(context), PrefsLocationSource(context))
        }
    }

}
