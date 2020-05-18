package io.github.websters_dog.weather_forecast_2

import android.content.pm.PackageManager
import android.util.Log
import io.github.websters_dog.weather_forecast_2.location.Coords
import io.github.websters_dog.weather_forecast_2.location.LocationPermissionException
import io.github.websters_dog.weather_forecast_2.location.LocationRepository
import io.github.websters_dog.weather_forecast_2.view.ForecastListView
import io.github.websters_dog.weather_forecast_2.weather.CurrentWeather
import io.github.websters_dog.weather_forecast_2.weather.WeatherRepository
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlin.system.exitProcess


class ForecastListPresenter(
    private val view: ForecastListView,
    private val locationRepository: LocationRepository,
    private val weatherRepository: WeatherRepository
) : ForecastListView.Presenter {

    private val compositeDisposable = CompositeDisposable()

    private var coords: Coords? = null

    init {
        view.presenter = this
    }

    override fun starWork() {
        view.setInProgress(true)
        retrieveLocation()
    }

    override fun stopWork() {
        view.setInProgress(false)
        compositeDisposable.clear()
    }

    override fun onRefreshRequested() {
        coords?.let { retrieveForecast(it) }
    }

    //todo remove duplication with CurrentWeatherPresenter
    private fun retrieveLocation() {
        //todo implement gps enabled check
        locationRepository.getLocation()
            .subscribe { coords, throwable ->
                if (throwable is LocationPermissionException) {
                    Log.d("[location]", "${javaClass.simpleName}.retrieveLocation : subscribe : $throwable")
                    view.requestLocationPermission()
                } else if (throwable != null || coords == null) {
                    Log.d("[location]", "${javaClass.simpleName}.retrieveLocation : subscribe : $throwable")
                    view.showMessage(R.string.location_error_message)
                } else {
                    Log.d("[location]", "${javaClass.simpleName}.retrieveLocation : subscribe : $coords")
                    this.coords = coords
                    retrieveForecast(coords)
                }
            }
            .addTo(compositeDisposable)
    }

    private fun retrieveForecast(coords: Coords) {
        view.setInProgress(true)
        weatherRepository.getForecast(coords)
            .subscribe({
                if (it == null) {
                    Log.d("[location]", "${javaClass.simpleName}.retrieveCurrentWeather : subscribe : $it")
                    view.showMessage(R.string.weather_loading_error_message)
                } else {
                    view.setData(it.list ?: listOf())
                    view.setInProgress(false)
                }
            }) {
                Log.d("[location]", "${javaClass.simpleName}.retrieveCurrentWeather : subscribe : $it")
                view.showMessage(R.string.weather_loading_error_message)
            }
            .addTo(compositeDisposable)
    }

    override fun onPermissionRequestResult(grantResults: IntArray) {
        if (grantResults.isNotEmpty()
            && grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            retrieveLocation()
        } else {
            exitProcess(1);
        }
    }

}