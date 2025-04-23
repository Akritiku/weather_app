package com.example.weather_app

data class WeatherResponse(
    val weather: List<Weather>,
    val main: Main,
    val wind: Wind,
    val sys: Sys
)

data class WeatherInfo(
    val main: String,
    val description: String,
    val icon: String
)

data class MainInfo(
    val temp: Double,
    val pressure: Int,
    val humidity: Int,
    val temp_max: Double,
    val temp_min: Double
)

data class WindInfo(
    val speed: Double
)

data class SysInfo(
    val sunrise: Long,
    val sunset: Long
)

