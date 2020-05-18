package io.github.websters_dog.weather_forecast_2.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import io.github.websters_dog.weather_forecast_2.R
import kotlinx.android.synthetic.main.weather_parameter_view.view.*

class WeatherParameterView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
    defStyleRes: Int = 0
) : LinearLayout(context, attrs, defStyle, defStyleRes) {

    init {
        LayoutInflater.from(context).inflate(R.layout.weather_parameter_view, this, true)

        attrs?.let {
            val typedArray = context.obtainStyledAttributes(
                it,
                R.styleable.WeatherParameterView, 0, 0)
            val iconRes = typedArray.getResourceId(
                R.styleable.WeatherParameterView_parameter_icon,
                R.drawable.ic_wb_cloudy)
            setIcon(iconRes)
            typedArray.recycle()

            orientation = VERTICAL
        }
    }

    fun setIcon(icon: Int) {
        parameter_icon.setImageResource(icon)
    }

    fun setText(text: String) {
        parameter_value.setText(text)
    }

    fun setText(text: Int) {
        parameter_value.setText(text)
    }


}