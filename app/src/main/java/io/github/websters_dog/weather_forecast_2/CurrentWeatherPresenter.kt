package io.github.websters_dog.weather_forecast_2

import android.content.pm.PackageManager
import android.util.Log
import io.github.websters_dog.weather_forecast_2.location.Coords
import io.github.websters_dog.weather_forecast_2.location.LocationPermissionException
import io.github.websters_dog.weather_forecast_2.location.LocationRepository
import io.github.websters_dog.weather_forecast_2.view.CurrentWeatherView
import io.github.websters_dog.weather_forecast_2.weather.CurrentWeather
import io.github.websters_dog.weather_forecast_2.weather.WeatherRepository
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlin.system.exitProcess


class CurrentWeatherPresenter(
    private val view: CurrentWeatherView,
    private val locationRepository: LocationRepository,
    private val weatherRepository: WeatherRepository
) : CurrentWeatherView.Presenter {

    private val compositeDisposable = CompositeDisposable()

    private var currentWeather: CurrentWeather? = null

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
                    retrieveCurrentWeather(coords)
                }
            }
            .addTo(compositeDisposable)
    }

    private fun retrieveCurrentWeather(coords: Coords) {
        weatherRepository.getCurrentWeather(coords)
            .subscribe({
                if (it == null) {
                    Log.d("[location]", "${javaClass.simpleName}.retrieveCurrentWeather : subscribe : $it")
                    view.showMessage(R.string.weather_loading_error_message)
                } else {
                    view.setCurrentWeather(it)
                    view.setInProgress(false)
                }
            }) {
                Log.d("[location]", "${javaClass.simpleName}.retrieveCurrentWeather : subscribe : $it")
                view.showMessage(R.string.weather_loading_error_message)
            }
            .addTo(compositeDisposable)
    }

    override fun onShareClicked() {
        currentWeather?.let {
            view.showShareActivity(
                R.string.current_weather_share_message,
                "${it.main?.temp ?: ""}°C | ${it.weather?.first()?.main ?: ""}"
            )
        }
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