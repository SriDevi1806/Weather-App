package com.example.weatherapp

data class LocationData(val results: List<Results>)

data class Results(val formatted: String) {

}
