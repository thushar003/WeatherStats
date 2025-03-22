package com.example.weatherstats.models


data class WeatherResponse(
    val location: WeatherLocation,
    val current: CurrentWeather
)

data class WeatherLocation(
    val name: String,
    val country: String
)

data class Astro(
    val sunrise: String,
    val sunset: String,
    val moonrise: String,
    val moonset: String,
    val moon_phase: String,
    val moon_illumination: Int
)

data class CurrentWeather(
    val temperature: Int,
    val weather_descriptions: List<String>,
    val weather_icons: List<String>,
    val astro: Astro
)