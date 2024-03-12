package com.example.weatherapplication

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather_assignment.models.Weather
import com.example.weatherapplication.repository.WeatherRepository
import kotlinx.coroutines.launch

class WeatherViewModel(private val repository: WeatherRepository) : ViewModel() {
    private val _weather = MutableLiveData<Weather>()
    val weather: LiveData<Weather> = _weather

    fun fetchWeather(query: String, callback: () -> Unit) {
        viewModelScope.launch {
            try {
                val result = repository.getWeather(query)
                _weather.value = result
                Log.d("response", result.toString())
            } catch (e: Exception) {
                Log.d("response", e.message.toString())
            }finally {
                callback.invoke()
            }
        }
    }
}