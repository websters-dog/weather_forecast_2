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


interface CurrentWeatherView {

    var presenter: Presenter
    fun setCurrentWeather(weather: CurrentWeather)
    fun setInProgress(inProgress: Boolean)
    fun requestLocationPermission()
    fun showShareActivity(textRes: Int, vararg formatArgs: Any)
    fun showMessage(textRes: Int)

    interface Presenter {
        fun stopWork()
        fun starWork()
        fun onShareClicked()
        fun onPermissionRequestResult(grantResults: IntArray)
    }
}


private const val LOCATION_PERMISSION_REQUEST = 9001


class CurrentWeatherFragment : Fragment(R.layout.fragment_current_weather), CurrentWeatherView {

    override lateinit var presenter: CurrentWeatherView.Presenter
    private lateinit var weather: CurrentWeather

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        share_text.setOnClickListener { presenter.onShareClicked() }
    }

    override fun onResume() {
        super.onResume()
        presenter.starWork()
    }

    override fun onPause() {
        super.onPause()
        presenter.stopWork()
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

    override fun requestLocationPermission() {
        activity?.let {
            val needExplanation = ActivityCompat
                .shouldShowRequestPermissionRationale(it, Manifest.permission.ACCESS_COARSE_LOCATION)

            if (needExplanation) {
                AlertDialog.Builder(it)
                    .setTitle(R.string.location_permission_explanation_title)
                    .setMessage(R.string.location_permission_explanation_message)
                    .setNeutralButton(android.R.string.ok) { _, _ -> /* do nothing */ }
                    .setOnDismissListener() { _ -> makeLocationPermissionRequest() }
                    .create()
                    .show()
            } else {
                makeLocationPermissionRequest()
            }
        }
    }

    override fun showShareActivity(textRes: Int, vararg formatArgs: Any) {
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, getString(textRes, *formatArgs))
        }
        startActivity(intent)
    }

    override fun showMessage(textRes: Int) {
        //todo replace with shackbar
        Toast.makeText(context, textRes,Toast.LENGTH_LONG).show()
    }

    private fun makeLocationPermissionRequest() {
        activity?.let {
            ActivityCompat.requestPermissions(
                it,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST -> {
                presenter.onPermissionRequestResult(grantResults)
            }
        }
    }


    companion object  {
        fun newInstance() = CurrentWeatherFragment()
    }

}

