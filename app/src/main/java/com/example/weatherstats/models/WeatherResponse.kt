package com.example.weatherstats.models


data class WeatherResponse(
    val location: WeatherLocation,
    val current: CurrentWeather
)

data class WeatherLocation(
    val name: String,
    val country: String
)

data class CurrentWeather(
    val temperature: Int,
    val weather_descriptions: List<String>
)