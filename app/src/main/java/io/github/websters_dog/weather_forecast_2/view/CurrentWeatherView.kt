package io.github.websters_dog.weather_forecast_2.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.squareup.picasso.Picasso
import io.github.websters_dog.weather_forecast_2.R
import io.github.websters_dog.weather_forecast_2.weather.CurrentWeather
import kotlinx.android.synthetic.main.fragment_current_weather.*
import kotlin.math.roundToInt


interface CurrentWeatherView {
    var callback: Callback
    fun setCurrentWeather(weather: CurrentWeather)
    fun setInProgress(inProgress: Boolean)

    interface Callback {
        fun onShareClicked()
    }
}


class CurrentWeatherFragment : Fragment(R.layout.fragment_current_weather), CurrentWeatherView {

    override lateinit var callback: CurrentWeatherView.Callback
    private lateinit var weather: CurrentWeather

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        share_text.setOnClickListener(::onShareClicked)
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

    //todo move to callback
    private fun onShareClicked(v: View) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(
                Intent.EXTRA_TEXT,
                getString(
                    R.string.current_weather_share_message,
                    "${weather.main?.temp ?: ""}°C | ${weather.weather?.first()?.main ?: ""}")
            )
        }
        startActivity(sendIntent)
    }


    companion object  {
        fun newInstance() = CurrentWeatherFragment()
    }

}

