package com.example.weather_app

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {
    @GET("weather")
    fun getWeatherData(
        @Query("q") cityName: String,
        @Query("apiKey") apiKey: String,
        @Query("units") units: String
    ): Call<WeatherResponse>
}
