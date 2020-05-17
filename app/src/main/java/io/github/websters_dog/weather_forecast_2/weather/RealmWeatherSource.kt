package io.github.websters_dog.weather_forecast_2.weather

import io.reactivex.Observable
import io.realm.Realm
import io.realm.RealmObject

class RealmWeatherSource : CachingWeatherSource {

    override fun getForecast(): Observable<Forecast> {
        return loadData()
    }

    override fun getCurrentWeather(): Observable<CurrentWeather> {
        return loadData()
    }

    private inline fun <reified T : RealmObject> loadData(): Observable<T> {
        return Observable.create { emitter ->
            Realm.getDefaultInstance().use { realm ->
                realm.where(T::class.java).findFirst()?.let { currentWeather ->
                    emitter.onNext(currentWeather.freeze())
                }
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