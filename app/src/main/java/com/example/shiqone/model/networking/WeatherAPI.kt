package com.example.shiqone.model.networking

import com.example.shiqone.model.Weather
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherAPI {

    @GET("forecast")
    fun getCurrentWeather(
        //?latitude=31.97&longitude=34.7534&hourly=temperature_2m
        @Query("latitude") latitude: Double = 31.97,
        @Query("longitude") longitude: Double = 34.7534,
        @Query("hourly") language: String = "temperature_2m",
    ): Call<Weather>
}
