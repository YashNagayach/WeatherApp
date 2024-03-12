package com.example.weatherapplication

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.weather_assignment.models.Weather
import com.example.weatherapplication.Utils.Constants.REQUEST_LOCATION_PERMISSION
import com.example.weatherapplication.Utils.convertToCelsiusString
import com.example.weatherapplication.Utils.getTodayDate
import com.example.weatherapplication.Utils.isInternetAvailable
import com.example.weatherapplication.Utils.showNoInternetDialog
import com.example.weatherapplication.databinding.ActivityMainBinding
import com.example.weatherapplication.network.RetrofitInstance
import com.example.weatherapplication.repository.WeatherRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.Locale


class MainActivity : AppCompatActivity() {
    lateinit var viewModel: WeatherViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val repository = WeatherRepository(RetrofitInstance.retrofit)
        val viewModelFactory = MyViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(WeatherViewModel::class.java)

        // Observe weather data
        viewModel.weather.observe(this) { weather ->
            if (weather != null) {
                updateUi(weather, binding)
            }
        }
        if (checkPermissions()) {
          getLastLocation()
        } else {
            requestPermissions()
        }

        binding.searchButton.setOnClickListener {
            val searchText = binding.searchCityEditText.text.toString()
            // Clear the EditText field
            binding.searchCityEditText.text.clear()

            // Check if the text is not empty
            if (searchText.isNotEmpty()) {
               callApi(searchText)
            }
        }
    }

    private fun updateUi(weather: Weather, binding: ActivityMainBinding) {
        val iconurl = "https://openweathermap.org/img/w/" + weather.weather[0].icon + ".png";

        binding.city.text = weather.name
        binding.temperature.text = convertToCelsiusString(weather.main.feels_like)
        binding.tempCondition.text = convertToCelsiusString(weather.main.temp)
        binding.date.text = getTodayDate()
        binding.condition.text = weather.weather[0].description
        Glide.with(this).load(iconurl).into(binding.weatherResource)
        binding.humidityValue.text = weather.main.humidity.toString()
        binding.windValue.text = weather.wind.speed.toString()
        binding.uvValue.text = weather.main.pressure.toString()
    }



    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(ACCESS_FINE_LOCATION),
            REQUEST_LOCATION_PERMISSION
        )
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val city = getCityName(location.latitude, location.longitude)
                    callApi(city)
                } else {
                    Toast.makeText(this, "Location not available", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun callApi(city: String) {
        if(isInternetAvailable(this)){
            viewModel.fetchWeather(city)
        }else{
            showNoInternetDialog(this)
        }
    }


    private fun getCityName(latitude: Double, longitude: Double): String {
        val geoCoder = Geocoder(this, Locale.getDefault())
        val addresses = geoCoder.getFromLocation(latitude, longitude, 1)
        return if (addresses?.isNotEmpty() == true) {
            addresses?.get(0)?.locality ?: "New Delhi"
        } else {
            "New Delhi"
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastLocation()
            } else {
                Toast.makeText(
                    this,
                    "Location permission denied, cannot retrieve current city",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}