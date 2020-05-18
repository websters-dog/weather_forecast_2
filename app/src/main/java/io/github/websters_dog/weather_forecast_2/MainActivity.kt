package io.github.websters_dog.weather_forecast_2

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import io.github.websters_dog.weather_forecast_2.location.LocationRepository
import io.github.websters_dog.weather_forecast_2.view.CurrentWeatherFragment
import io.github.websters_dog.weather_forecast_2.view.ForecastListFragment
import io.github.websters_dog.weather_forecast_2.weather.WeatherRepository
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        toolbar.title = title

        val pageAdapter = WeatherPagesAdapter(this, supportFragmentManager)

        val currentWeatherFragment = CurrentWeatherFragment.newInstance()
        pageAdapter.addFragment(currentWeatherFragment)

        val forecastListFragment = ForecastListFragment.newInstance()
        pageAdapter.addFragment(forecastListFragment)

        view_pager.adapter = pageAdapter

        tabs.setupWithViewPager(view_pager)
        tabs.getTabAt(0)
            ?.setIcon(R.drawable.ic_sun)
            ?.setText(R.string.tab_name_now)
        tabs.getTabAt(1)
            ?.setIcon(R.drawable.ic_stormy_weather)
            ?.setText(R.string.tab_name_forecast)

        val locationRepository = LocationRepository.getInstance(applicationContext)
        val weatherRepository = WeatherRepository.getInstance()

        val currentWeatherPresenter = CurrentWeatherPresenter(
            currentWeatherFragment,
            locationRepository,
            weatherRepository)
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
