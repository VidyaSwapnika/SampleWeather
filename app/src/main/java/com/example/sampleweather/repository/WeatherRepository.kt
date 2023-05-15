package com.example.sampleweather.repository

import com.example.sampleweather.service.RetrofitService

class WeatherRepository constructor(private val retrofitService: RetrofitService) {

    fun getWeatherDataByLatLon(lat: Double, lon: Double, appId: String) =
        retrofitService.getWeatherDataByLatLon(lat, lon, appId)

    fun getWeatherDataByCity(q : String, appId: String) =
        retrofitService.getWeatherDataByCity(q, appId)

}