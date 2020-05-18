package io.github.websters_dog.weather_forecast_2

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import io.github.websters_dog.weather_forecast_2.location.LocationIsNotAvailableException
import io.github.websters_dog.weather_forecast_2.location.LocationPermissionException
import io.github.websters_dog.weather_forecast_2.location.LocationRepository
import io.github.websters_dog.weather_forecast_2.view.CurrentWeatherFragment
import io.github.websters_dog.weather_forecast_2.view.ForecastListFragment
import io.github.websters_dog.weather_forecast_2.view.ForecastListView
import io.github.websters_dog.weather_forecast_2.weather.WeatherRepository
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.activity_main.*


private const val LOCATION_PERMISSION_REQUEST = 9001

class ItemListActivity : AppCompatActivity() {

    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        toolbar.title = title

        retrieveLocation()


        var adapter = WeatherPagesAdapter(this, supportFragmentManager)
        val currentWeatherFragment = CurrentWeatherFragment.newInstance()
        adapter.addFragment(currentWeatherFragment)
        val forecastListFragment = ForecastListFragment.newInstance()
        adapter.addFragment(forecastListFragment)
        view_pager.adapter = adapter

        tabs.setupWithViewPager(view_pager)
        tabs.getTabAt(0)
            ?.setIcon(R.drawable.ic_sun)
            ?.setText(R.string.tab_name_now)
        tabs.getTabAt(1)
            ?.setIcon(R.drawable.ic_stormy_weather)
            ?.setText(R.string.tab_name_forecast)


        WeatherRepository.getInstance().getCurrentWeather()
            .subscribe{
                Log.d("[weather]", "${javaClass.simpleName}.onCreate : getForecast : $it")
                currentWeatherFragment.setCurrentWeather(it)
            }
            .addTo(compositeDisposable)

        WeatherRepository.getInstance().getForecast()
            .subscribe{
                Log.d("[weather]", "${javaClass.simpleName}.onCreate : getForecast : $it")
                forecastListFragment.setData(it.list?.toList() ?: listOf())
            }
            .addTo(compositeDisposable)
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }

    private fun retrieveLocation() {
        val lr = LocationRepository.getInstance(this)
        lr.invalidateCache()
        val disposable = lr.getLocation()
            .subscribe { coords, throwable ->
                when (throwable) {
                    null -> {
                        Log.d("[location]", "${javaClass.simpleName}.retrieveLocation : subscribe : $coords")
                        Toast.makeText(this, coords.toString(), Toast.LENGTH_LONG).show()
                    }
                    is LocationPermissionException -> {
                        Log.e("[location]", throwable.toString())
                        Toast.makeText(this, throwable.toString(), Toast.LENGTH_LONG).show()

                        checkLocationPermission()
                    }
                    is LocationIsNotAvailableException -> {
                        Log.e("[location]", throwable.toString())
                        Toast.makeText(this, throwable.toString(), Toast.LENGTH_LONG).show()
                    }
                }
            }
        compositeDisposable.add(disposable)
    }

    private fun checkLocationPermission(): Boolean {
        val needExplanation = ActivityCompat
            .shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        if (needExplanation) {
            AlertDialog.Builder(this)
                .setTitle(R.string.location_permission_explanation_title)
                .setMessage(R.string.location_permission_explanation_message)
                .setPositiveButton(android.R.string.ok) { _, _ -> makeLocationPermissionRequest() }
                .create()
                .show()
        } else {
            makeLocationPermissionRequest()
        }
        return false
    }

    private fun makeLocationPermissionRequest() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
            LOCATION_PERMISSION_REQUEST)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST -> {
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    retrieveLocation()
                } else {
                    //todo show warning or something else
                }
                return
            }
        }
    }
}

private class WeatherPagesAdapter(
    context: Context,
    fm: FragmentManager
) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val fragments = mutableListOf<Fragment>()

    fun addFragment(fragment: Fragment) { fragments += fragment }

    override fun getItem(position: Int) =  fragments[position]

    override fun getCount() = fragments.size
}
