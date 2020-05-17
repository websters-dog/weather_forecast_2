package io.github.websters_dog.weather_forecast_2.weather

import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


private const val UNITS = "metric"
private const val API_KEY = "5dfcd029ffe0ab05a5a2ce63306b8190"


interface OpenWeatherMapApiService {

    @GET("forecast?appid=$API_KEY&units=$UNITS")
    fun LoadForecast(
        @Query("lat") lat: Float,
        @Query("lon") lon: Float
    ): Observable<Forecast>

    @GET("weather?appid=$API_KEY&units=$UNITS")
    fun LoadCurrentWeather(
        @Query("lat") lat: Float,
        @Query("lon") lon: Float
    ): Observable<CurrentWeather>

    companion object {
        fun create(): OpenWeatherMapApiService {
            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

            return Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .client(client)
                .build()
                .create(OpenWeatherMapApiService::class.java)
        }
    }
    //Type retrofit2.CompletableFutureCallAdapterFactory$ResponseCallAdapter is defined multiple times:
    // /home/tandat/develop/weather_forecast_2/app/build/intermediates/external_libs_dex_archive/debug/out/9f82b212ddd91b04a36d9bd74649a10e689cc86931e422848b454b74d890468b_2.jar:classes.dex,
    // /home/tandat/develop/weather_forecast_2/app/build/intermediates/external_libs_dex_archive/debug/out/9f82b212ddd91b04a36d9bd74649a10e689cc86931e422848b454b74d890468b.jar:__classes.dex

    // https://samples.openweathermap.org/data/2.5/find?q=London&units=metric&appid=439d4b804bc8187953eb36d2a8c26a02
}