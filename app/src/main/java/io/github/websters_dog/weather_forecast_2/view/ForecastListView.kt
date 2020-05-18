package io.github.websters_dog.weather_forecast_2.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import io.github.websters_dog.weather_forecast_2.R
import io.github.websters_dog.weather_forecast_2.weather.ForecastItem
import kotlinx.android.synthetic.main.fragment_forecast_list.*
import kotlinx.android.synthetic.main.item_forecast.view.*
import kotlinx.android.synthetic.main.item_forecast_day.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt


interface ForecastListView : BasicView<ForecastListView.Presenter>  {
    fun setData(forecastItems: List<ForecastItem>)

    interface Presenter : BasicView.Presenter {
        fun onRefreshRequested()
    }
}


class ForecastListFragmentView
    : AbstractBasicViewFragment<ForecastListView.Presenter>(R.layout.fragment_forecast_list),
    ForecastListView {

    private lateinit var adapter: ForecastAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = ForecastAdapter()
        items_list.adapter = adapter
        val itemDecorator = DividerItemDecoration(view.context, DividerItemDecoration.VERTICAL)
        ContextCompat.getDrawable(view.context, R.drawable.list_separator)?.let {
            itemDecorator.setDrawable(it)
        }
        swipe_to_refresh.setOnRefreshListener { presenter.onRefreshRequested() }
    }

    override fun setData(forecastItems: List<ForecastItem>) {
        adapter.setData(forecastItems)
    }

    override fun setInProgress(inProgress: Boolean) {
        swipe_to_refresh.isRefreshing = inProgress
    }


    private class ForecastAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        val timeOfDayFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val dayOfWeekFormat = SimpleDateFormat("EEEE", Locale.getDefault())
        val now = GregorianCalendar.getInstance()

        private val data = mutableListOf<Any>()

        fun setData(forecastItems: List<ForecastItem>) {
            data.clear()

            var prevCalendar: Calendar? = null
            for (f in forecastItems) {
                val calendar = GregorianCalendar(TimeZone.getTimeZone("UTC"))
                calendar.timeInMillis = f.getMillisDt()
                calendar.timeZone = TimeZone.getDefault()

                if (prevCalendar == null
                    || prevCalendar.get(Calendar.DAY_OF_YEAR) != calendar.get(Calendar.DAY_OF_YEAR)) {

                    prevCalendar = calendar
                    data.add(calendar)
                }
                data.add(Pair(f, calendar))
            }

            notifyDataSetChanged()
        }

        override fun getItemCount() = data.size

        override fun getItemViewType(position: Int): Int {
            return when(data[position]) {
                is Calendar -> 0
                is Pair<*, *> -> 1
                else -> throw IllegalArgumentException()
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return when(viewType) {
                0 -> ForecastDayViewHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_forecast_day, parent, false))
                1 -> ForecastItemViewHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_forecast, parent, false))
                else -> throw IllegalArgumentException()
            }
        }

        override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
            when(viewHolder) {
                is ForecastDayViewHolder -> viewHolder.bindDate(data[position] as Calendar)
                is ForecastItemViewHolder ->
                    @Suppress("UNCHECKED_CAST")
                    viewHolder.bindForecastItem(data[position] as Pair<ForecastItem, Calendar>)
            }
        }


        private inner class ForecastItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

            fun bindForecastItem(dataPair: Pair<ForecastItem, Calendar>) {
                val forecastItem = dataPair.first
                Picasso.get()
                    .load(getWeatherIconUrl(forecastItem.weather?.first()))
                    .into(itemView.weather_icon)

                itemView.time_text.text = timeOfDayFormat.format(dataPair.second.time)
                itemView.weather_text.text = forecastItem.weather?.first()?.main ?: ""
                itemView.temp_text.text = itemView.context.getString(
                    R.string.forecast_list_degree_format,
                    forecastItem.main?.temp?.roundToInt() ?: 0)
            }
        }


        private inner class ForecastDayViewHolder(view: View) : RecyclerView.ViewHolder(view) {

            fun bindDate(calendar: Calendar) {
                if (calendar.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR)) {
                    itemView.day_text.setText(R.string.forecast_list_today)
                } else {
                    itemView.day_text.text = dayOfWeekFormat.format(calendar.time)
                }
            }
        }

    }


    companion object {
        fun newInstance() = ForecastListFragmentView()
    }
}