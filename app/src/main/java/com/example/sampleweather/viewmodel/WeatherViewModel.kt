package com.example.sampleweather.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sampleweather.data.WeatherResponse
import com.example.sampleweather.repository.WeatherRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeatherViewModel constructor(private val repository: WeatherRepository) : ViewModel() {

    val weatherCityResponse = MutableLiveData<WeatherResponse>()
    val weatherLatLonResponse = MutableLiveData<WeatherResponse>()
    val errorMessage = MutableLiveData<String>()


    fun getWeatherData(city : String) {
        val response = repository.getWeatherDataByCity(city ,"31aeff340ac2452df20a7f2678bacb29")

        response.enqueue( object : Callback<WeatherResponse>{
            override fun onResponse(
                call: Call<WeatherResponse>,
                response: Response<WeatherResponse>
            ) {
                weatherCityResponse.postValue(response.body())
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                errorMessage.postValue(t.message)
            }

        })
    }

    fun getCurrentLocationWeatherData(latitude : Double, longitude : Double ) {
        val response = repository.getWeatherDataByLatLon(latitude, longitude, "31aeff340ac2452df20a7f2678bacb29")

        response.enqueue( object : Callback<WeatherResponse>{
            override fun onResponse(
                call: Call<WeatherResponse>,
                response: Response<WeatherResponse>
            ) {
                weatherLatLonResponse.postValue(response.body())
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                errorMessage.postValue(t.message)
            }

        })

    }
}