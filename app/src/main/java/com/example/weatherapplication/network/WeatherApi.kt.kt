package com.example.weatherapplication.network

import com.example.weather_assignment.models.Weather
import com.example.weatherapplication.Utils.Constants
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET(value = "data/2.5/weather")
    suspend fun getWeather(
        @Query("q") query: String,
        @Query("units") unit: String = "metric",
        @Query("appid") appid: String = Constants.API_KEY,
    ): Weather
}