package com.example.weatherapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.weatherapp.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private lateinit var searchHistoryAdapter: SearchHistoryAdapter
    private lateinit var searchHistoryDao: SearchHistoryDao
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        requestLocationPermission()
        setSearchCityListener()
        searchHistoryDao = (application as MyApplication).database.searchHistoryDao()

        // Initialize the adapter
       // searchHistoryAdapter = SearchHistoryAdapter(searchHistoryList)

            // Trigger to fetch data and navigate to the second activity
            binding.History.setOnClickListener {
                GlobalScope.launch(Dispatchers.Main) {
                    val searchHistoryList = searchHistoryDao.getRecentSearchHistory()
                      if (searchHistoryList.isNotEmpty()) {
//                        searchHistoryAdapter.updateList(searchHistoryList)
                       navigateToSecondActivity(searchHistoryList)


                       Toast.makeText(this@MainActivity, "Search history", Toast.LENGTH_SHORT).show()
//
                    } else {
                        Toast.makeText(this@MainActivity, "Search history is empty", Toast.LENGTH_SHORT).show()
                    }
                }

            }
        }
    private fun navigateToSecondActivity(searchHistoryList: List<SearchHistory>) {
        val intent = Intent(this, HistoryActivity::class.java)
        // Pass the data to the second activity using Intent
        intent.putParcelableArrayListExtra("SEARCH_HISTORY_LIST", ArrayList(searchHistoryList))
        startActivity(intent)

    }
    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            // Permission already granted, proceed with location retrieval
            getLocation()
        }
    }
    private fun setSearchCityListener() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchDataWeather(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Handle text change if needed
                return true
            }
        })
    }
    private fun getLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        reverseGeocode(location.latitude, location.longitude)
                        fetchDataWeatherForLocation(location.latitude, location.longitude)
                        Toast.makeText(this, "permission granted", Toast.LENGTH_SHORT).show()
                    } else {
                        binding.city.text = "Unable to retrieve location."
                    }
                }
                .addOnFailureListener { exception ->
                    binding.city.text = "Error getting location: ${exception.message}"
                }
        }
    }
    private suspend fun saveSearchHistory(city: String,date: String, time: String, temp: String) {
        val searchHistory = SearchHistory(city = city,date = date, time = time, temp = temp)
        searchHistoryDao.insert(searchHistory)
        Log.d("Database", "Inserted data: $city,$date, $time, $temp")
    }
    private fun fetchDataWeatherForLocation(latitude: Double, longitude: Double) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)

        val response =
            retrofit.getWeatherDataByLocation(latitude, longitude, "e87152cc4ee1d143727b88008658ccc8", "metric")

        response.enqueue(object : Callback<WeatherApp> {
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    updateUI(responseBody, "Current Location")
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Failed to fetch weather data",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error fetching location : ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchDataWeather(city: String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)

        val response = retrofit.getWeatherData(city, "e87152cc4ee1d143727b88008658ccc8", "metric")

        response.enqueue(object : Callback<WeatherApp> {
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    updateUI(responseBody, city)
                    //add search
                    CoroutineScope(Dispatchers.Main).launch {
                        saveSearchHistory(
                            city,
                            date(),
                            time(System.currentTimeMillis()),
                            responseBody.main.temp.toString())
                        //displayRecentSearchHistory()
                    }

                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Failed to fetch weather data",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateUI(responseBody: WeatherApp, city: String) {
        val temperature = responseBody.main.temp.toString()
        val humidity = responseBody.main.humidity
        val windspeed = responseBody.wind.speed
        val sunRise = responseBody.sys.sunrise.toLong()
        val sunSet = responseBody.sys.sunset.toLong()
        val seaLevel = responseBody.main.pressure
        val condition = responseBody.weather.firstOrNull()?.main ?: "unknown"
        val maxTemp = responseBody.main.temp_max
        val minTemp = responseBody.main.temp_min

        binding.temp.text = "$temperature °C"
        binding.weather.text = condition
        binding.humidilty.text = "$humidity %"
        binding.windspeed.text = "$windspeed m/s"
        binding.sunrise.text = "${time(sunRise)}"
        binding.sunset.text = "${time(sunSet)}"
        binding.sea.text = "$seaLevel hPa"
        binding.condition.text = condition
        binding.maxTemp.text = "$maxTemp °C"
        binding.minTemp.text = "$minTemp °C"
        binding.city.text = "$city"
        binding.day.text = dayName(System.currentTimeMillis())
        binding.date.text = date()

        changeImageAccordingToWeatherCondition(condition)
    }

    private fun changeImageAccordingToWeatherCondition(conditions: String) {
        when (conditions) {
            "Clear Sky", "Sunny", "Clear" -> {
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
            "Partly Clouds", "Clouds", "Overcast", "Mist", "Foggy" -> {
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }
            "Light Rain", "Drizzle", "Moderate Rain", "Showers", "Heavy Rain", "Rain" -> {
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }
            "Light Snow", "Moderate Snow", "Heavy Snow", "Blizzard" -> {
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }
            else -> {
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
        }
        binding.lottieAnimationView.playAnimation()
    }

    private fun date(): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun time(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    private fun dayName(timestamp: Long): String {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }


    private fun reverseGeocode(latitude: Double, longitude: Double) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.opencagedata.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(ReverseGeocodingService::class.java)

        val call = retrofit.reverseGeocode("$latitude,$longitude", "f2191fa377eb464fa740f0b0576ce0ac")

        call.enqueue(object : Callback<LocationData> {
            override fun onResponse(call: Call<LocationData>, response: Response<LocationData>) {
                if (response.isSuccessful) {
                    val results = response.body()?.results
                    if (results != null && results.isNotEmpty()) {
                        val locationName = results[0].formatted
                        binding.city.text = "${locationName.substring(0,31)}"
                    } else {
                        binding.city.text = "Location data not found."
                    }
                } else {
                    binding.city.text = "Error fetching location data."
                }
            }

            override fun onFailure(call: Call<LocationData>, t: Throwable) {
                binding.city.text = "Failed to fetch location data: ${t.message}"
            }
        })
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, proceed with location retrieval
                    getLocation()
                } else {
                    // Permission denied, handle accordingly
                    binding.city.text = "Location permission denied."
                }
            }
        }
    }


}
