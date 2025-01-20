package com.example.weatherstats.api

import com.example.weatherstats.models.WeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("current")
    fun getCurrentWeather(
        @Query("access_key") apiKey: String,
        @Query("query") location: String
    ): Call<WeatherResponse>
}