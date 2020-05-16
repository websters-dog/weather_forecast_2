package io.github.websters_dog.weather_forecast_2.location

import android.content.Context
import android.util.Log
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleSource


class LocationRepository private constructor(
    private val cachingSource: CachingLocationSource,
    private val basicSources: List<LocationSource>
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
        return getLocation(cachingSource)
    }

    private fun getLocation(source: LocationSource): Single<Coords> {
        Log.d("[location]", "${javaClass.simpleName}.getLocation : ${source.javaClass.simpleName}")

        var resumeWith: ((Throwable) -> SingleSource<Coords>)? = null
        when {
            source == cachingSource && basicSources.isNotEmpty() -> {
                resumeWith = {
                    Log.d("[location]", "${javaClass.simpleName}.getLocation : onErrorResumeWith : $it")
                    getLocation(basicSources[0])
                }
            }
            basicSources.last() != source -> {
                resumeWith = {
                    Log.d("[location]", "${javaClass.simpleName}.getLocation : onErrorResumeWith : $it")
                    getLocation(basicSources[basicSources.indexOf(source) + 1])
                }
            }
        }

        return if (resumeWith != null) source.getLocation().onErrorResumeNext(resumeWith)
            else source.getLocation()
    }


    companion object {
        fun getInstance(context: Context): LocationRepository {
            return LocationRepository(
                PrefsLocationSource(context),
                listOf(LastKnownLocationSource(context), GPSLocationSource(context))
            )
        }
    }


}
