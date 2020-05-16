package io.github.websters_dog.weather_forecast_2.location

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.preference.PreferenceManager
import io.reactivex.rxjava3.core.Single


private const val LAT_PREF_KEY = "PrefsLocationSource.LAT_KEY"
private const val LNG_PREF_KEY = "PrefsLocationSource.LNG_KEY"


class PrefsLocationSource (
    context: Context
) : CachingLocationSource {

    private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    private fun cacheLocation(latitude: Float, longitude: Float) {
        Log.d("[location]", "${javaClass.simpleName}.cacheLocation : $latitude;$longitude")
        val editor = prefs.edit()
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

    override fun getLocation(): Single<Coords> {
        Log.d("[location]", "${javaClass.simpleName}.getLocation")
        return Single.create {
            val lat = prefs.getFloat(LAT_PREF_KEY, Float.NaN)
            val lng = prefs.getFloat(LNG_PREF_KEY, Float.NaN)

            if (!lat.isNaN() && !lng.isNaN()) {
                it.onSuccess(Coords(lat, lng))
            } else {
                it.onError(NoLocationFoundException())
            }
        }
    }

}