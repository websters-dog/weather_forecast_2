package io.github.websters_dog.weather_forecast_2.location

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.preference.PreferenceManager
import io.reactivex.Single


private const val TIME_PREF_KEY = "PrefsLocationSource.TIME_KEY"
private const val LAT_PREF_KEY = "PrefsLocationSource.LAT_KEY"
private const val LNG_PREF_KEY = "PrefsLocationSource.LNG_KEY"

private const val MAX_VALID_TIME = 1000 * 60 * 10


class PrefsLocationSource (
    context: Context
) : CachingLocationSource {

    private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    override fun getLocation(): Single<Coords> {
        Log.d("[location]", "${javaClass.simpleName}.getLocation")
        return Single.create {
            val time = prefs.getLong(TIME_PREF_KEY, Long.MIN_VALUE)
            val lat = prefs.getFloat(LAT_PREF_KEY, Float.NaN)
            val lng = prefs.getFloat(LNG_PREF_KEY, Float.NaN)

            if (time + MAX_VALID_TIME >= System.currentTimeMillis()
                && !lat.isNaN() && !lng.isNaN()) {

                it.onSuccess(Coords(lat, lng))

            } else {
                it.onError(NoLocationFoundException())
            }
        }
    }

    private fun cacheLocation(latitude: Float, longitude: Float) {
        Log.d("[location]", "${javaClass.simpleName}.cacheLocation : $latitude;$longitude")
        val editor = prefs.edit()
        editor.putLong(TIME_PREF_KEY, System.currentTimeMillis())
        editor.putFloat(LAT_PREF_KEY, latitude)
        editor.putFloat(LNG_PREF_KEY, longitude)
        editor.apply()
    }

    override fun cacheLocation(coords: Coords) {
        Log.d("[location]", "${javaClass.simpleName}.cacheLocation : $coords")
        cacheLocation(coords.latitude, coords.longitude)
    }

    override fun invalidateCache() {
        Log.d("[location]", "${javaClass.simpleName}.invalidateCache")
        cacheLocation(Float.NaN, Float.NaN)
    }

}