package com.example.weatherstats.models

import android.location.Location

data class WeatherResponse(
    val location: Location,
    val current: CurrentWeather
)

data class Location(
    val name: String,
    val country: String
)

data class CurrentWeather(
    val temperature: Int,
    val weather_descriptions: List<String>
)