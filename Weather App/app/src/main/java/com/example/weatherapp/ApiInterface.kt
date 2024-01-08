package com.example.weatherapp


import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

//Data get karny k liye
interface ApiInterface {
    @GET("weather")
    fun getWeatherData(
        @Query("q") city:String,
        @Query("appid") appid:String,
        @Query("units") units:String
    ) : Call<WeatherApp>
    @GET("weather")
    fun getWeatherDataByLocation(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String
    ): Call<WeatherApp>

}

