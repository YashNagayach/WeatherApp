package com.example.weatherapplication.repository

import com.example.weather_assignment.models.Weather
import com.example.weatherapplication.network.WeatherApi

class WeatherRepository(private val weatherApi: WeatherApi) {
    suspend fun getWeather(query: String): Weather {
        return weatherApi.getWeather(query)
    }
}