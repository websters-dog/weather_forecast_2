package io.github.websters_dog.weather_forecast_2.view

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.squareup.picasso.Picasso
import io.github.websters_dog.weather_forecast_2.R
import io.github.websters_dog.weather_forecast_2.weather.CurrentWeather
import kotlinx.android.synthetic.main.fragment_current_weather.*
import kotlin.math.roundToInt


interface CurrentWeatherView : BasicView<CurrentWeatherView.Presenter> {

    fun setCurrentWeather(weather: CurrentWeather)

    interface Presenter : BasicView.Presenter {
        fun onShareClicked()
    }
}


private const val LOCATION_PERMISSION_REQUEST = 9001


class CurrentWeatherFragmentView
    : AbstractBasicViewFragment<CurrentWeatherView.Presenter>(R.layout.fragment_current_weather),
    CurrentWeatherView {

    private lateinit var weather: CurrentWeather

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        share_text.setOnClickListener { presenter.onShareClicked() }
    }

    override fun setCurrentWeather(weather: CurrentWeather) {
        this.weather = weather

        val iconUrl = Uri.parse(getWeatherIconUrl(weather.weather?.first()))
        Log.d("[weather]", iconUrl.toString())
        Picasso.get().isLoggingEnabled = true
        Picasso.get()
            .load(iconUrl)
            .into(weather_icon_big)

        place_text.text = getString(
            R.string.current_weather_place,
            weather.name,
            weather.sys?.country ?: "")

        weather_text.text = getString(
            R.string.current_weather_temp_weather,
            weather.main?.temp?.roundToInt() ?: 0,
            weather.weather?.first()?.main ?: "")

        rain_value.setText(
            getString(
                R.string.current_weather_precipitation_mm,
                weather.precipitation?.value ?: 0f))

        humidity.setText(
            getString(
                R.string.current_weather_humidity_percents,
                weather.main?.humidity?.roundToInt() ?: 0))

        pressure.setText(
            getString(
                R.string.current_weather_pressure_hpa,
                weather.main?.pressure?.roundToInt() ?: 0))

        wind_speed.setText(
            getString(
                R.string.current_weather_wind_speed_meter_per_second,
                weather.wind?.speed ?: 0f))

        wind_direction.setText(getWindDirectionTextRes(weather.wind?.deg ?: 0f))
    }

    override fun setInProgress(inProgress: Boolean) {
        root.visibility = if (inProgress) View.GONE else View.VISIBLE
        progress.visibility = if (inProgress) View.VISIBLE else View.GONE
    }


    companion object  {
        fun newInstance() = CurrentWeatherFragmentView()
    }

}

