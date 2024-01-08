package com.example.weatherapp

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ReverseGeocodingService {
    @GET("geocode/v1/json")
    fun reverseGeocode(
        @Query("q") latlng: String,
        @Query("key") key: String
    ): Call<LocationData>
}