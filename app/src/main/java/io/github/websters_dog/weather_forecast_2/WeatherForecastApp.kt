package io.github.websters_dog.weather_forecast_2

import android.app.Application
import io.realm.Realm


class WeatherForecastApp : Application() {

    override fun onCreate() {
        super.onCreate()

        Realm.init(this)
    }
}