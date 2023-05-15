package com.example.sampleweather

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.sampleweather.data.Weather
import com.example.sampleweather.databinding.SampleWeatherBinding
import com.example.sampleweather.repository.WeatherRepository
import com.example.sampleweather.service.RetrofitService
import com.example.sampleweather.viewmodel.WeatherViewModel
import com.example.sampleweather.viewmodel.WeatherViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.squareup.picasso.Picasso
import java.util.*

class SampleWeatherActivity : AppCompatActivity() {

    private lateinit var binding: SampleWeatherBinding

    lateinit var viewModel: WeatherViewModel

    private val retrofitService = RetrofitService.getInstance()

    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    private val permissionId = 2

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = SampleWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this,
            WeatherViewModelFactory(WeatherRepository(retrofitService))
        )[WeatherViewModel::class.java]

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        binding.btnSearch.setOnClickListener {
            viewModel.getWeatherData(binding.etCity.text.toString())
        }
        getLocation()
        binding.btnCurrentLocation.setOnClickListener {
            viewModel.getCurrentLocationWeatherData(latitude, longitude)

        }

        viewModel.weatherCityResponse.observe(this, Observer {
            // loading weather data from response
            loadWeatherData(it.weather[0])
        })

        viewModel.weatherLatLonResponse.observe(this, Observer {
            // loading weather data from response
            loadWeatherData(it.weather[0])
        })
    }

    private fun loadWeatherData(weather: Weather) {
        val image = "https://openweathermap.org/img/wn/" + weather.icon + "@2x.png"

        Picasso.get()
            .load(image)
            .resize(300, 300)
            .into(binding.ivWeatherIcon);
        binding.etCity.text.clear()
        binding.tvWeatherMain.text = weather.main
        binding.tvWeatherDescription.text = weather.description

    }


    @SuppressLint("MissingPermission")
    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
                    if (location != null) {
                        latitude = location.latitude
                        longitude = location.longitude
                    }
                }
            } else {
                Toast.makeText(this, "Please turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }


    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                ACCESS_COARSE_LOCATION,
                ACCESS_FINE_LOCATION
            ),
            permissionId
        )
    }


    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == permissionId) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            }
        }
    }
}