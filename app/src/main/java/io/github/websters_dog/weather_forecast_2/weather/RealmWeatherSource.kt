package io.github.websters_dog.weather_forecast_2.weather

import io.github.websters_dog.weather_forecast_2.location.Coords
import io.github.websters_dog.weather_forecast_2.location.distanceTo
import io.reactivex.Observable
import io.realm.Realm
import io.realm.RealmObject

private const val MAX_VALID_DISTANCE = 1f
private const val MAX_VALID_TIME = 1000 * 60 * 60

class RealmWeatherSource : CachingWeatherSource {

    override fun getForecast(coords: Coords): Observable<Forecast> {
        return loadData(coords)
    }

    override fun getCurrentWeather(coords: Coords): Observable<CurrentWeather> {
        return loadData(coords)
    }

    private inline fun <reified T> loadData(
        coords: Coords
    ): Observable<T> where T : RealmObject, T : DataInfo {
        return Observable.create { emitter ->
            Realm.getDefaultInstance().use { realm ->
                realm.where(T::class.java).findFirst()?.let { data ->
                    if (data.getCoords() == null
                        || data.getCoords() distanceTo coords > MAX_VALID_DISTANCE
                        || data.getMillisDt() + MAX_VALID_TIME <= System.currentTimeMillis()) {
                        realm.executeTransaction {
                            realm.delete(T::class.java)
                        }
                        emitter.onError(NoWeatherCacheException())
                    }
                    emitter.onNext(data.freeze())
                } ?: emitter.onError(NoWeatherCacheException())
            }
        }
    }

    override fun cacheForecast(forecast: Forecast) {
        cacheData(forecast)
    }

    override fun cacheCurrentWeather(currentWeather: CurrentWeather) {
        cacheData(currentWeather)
    }

    private inline fun <reified T : RealmObject> cacheData(data: T) {
        Realm.getDefaultInstance().use { realm ->
            realm.executeTransactionAsync {
                it.delete(T::class.java)
                realm.copyToRealm(data)
            }
        }
    }

    override fun invalidateCache() {
        Realm.getDefaultInstance().use { realm ->
            realm.executeTransactionAsync {
                it.deleteAll()
            }
        }
    }

}