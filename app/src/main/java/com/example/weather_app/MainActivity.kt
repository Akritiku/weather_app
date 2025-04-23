package com.example.weather_app

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
//import androidx.room.util.query
import com.example.weather_app.databinding.ActivityMainBinding
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fetchWeatherData("Jaipur")
        searchCity()
    }

    private fun searchCity() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                if (!p0.isNullOrEmpty()) {
                    fetchWeatherData(p0)
                }
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return true
            }
        })
    }


    private fun fetchWeatherData(cityName: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(ApiInterface::class.java)
        val call = api.getWeatherData(cityName, "3e50dd37d74c390e7ff87598a8bcec96", "metric")

        call.enqueue(object : Callback<WeatherResponse> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()

                    val temperature = responseBody?.main?.temp
                    val humidity = responseBody?.main?.humidity
                    val windSpeed = responseBody?.wind?.speed
                    val sunRise = responseBody?.sys?.sunrise?.toLong()
                    val sunSet = responseBody?.sys?.sunset?.toLong()
                    val seaLevel = responseBody?.main?.pressure
                    val condition = responseBody?.weather?.firstOrNull()?.main?.lowercase(Locale.ROOT) ?: "unknown"
                    val maxTemp = responseBody?.main?.temp_max
                    val minTemp = responseBody?.main?.temp_min

                    binding.temp.text = "$temperature °C"
                    binding.weather.text = condition
                    binding.MaxTemp.text = "Max Temp: $maxTemp °C"
                    binding.MinTemp.text = "Min Temp: $minTemp °C"
                    binding.humidity.text = "$humidity %"
                    binding.wind.text = "$windSpeed m/s"
                    binding.sunrise.text = "${sunRise?.let { time(it) }}"
                    binding.sunset.text = "${sunSet?.let { time(it) }}"
                    binding.sea.text = "$seaLevel hPa"
                    binding.conditions.text = condition
                    binding.day.text = dayName(System.currentTimeMillis())
                    binding.date.text = date()
                    binding.cityName.text = cityName
//                    binding.lottieAnimationView.playAnimation()


                    Toast.makeText(this@MainActivity, "Temperature: $temperature°C", Toast.LENGTH_LONG).show()
                    changeImagsaccordingtoWeatherCondition(condition)
                } else {
                    Log.e("WeatherApp", "API Error: ${response.code()}")
                    Toast.makeText(this@MainActivity, "API Error: ${response.code()}", Toast.LENGTH_SHORT).show()

                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                Log.e("WeatherApp", "Network Error: ${t.message}")
                Toast.makeText(this@MainActivity, "Failed to connect: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun changeImagsaccordingtoWeatherCondition(condition: String) {
        val weather = condition.lowercase(Locale.ROOT)

        when {
            weather.contains("haze") || weather.contains("mist") || weather.contains("fog") -> {
                binding.root.setBackgroundResource(R.drawable.colud_background) // Make sure this drawable exists!
                binding.lottieView.setAnimation(R.raw.cloud)
                binding.lottieView.playAnimation()
            }
            weather.contains("clear") -> {
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieView.setAnimation(R.raw.sun)
                binding.lottieView.playAnimation()
            }
            weather.contains("cloud") -> {
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieView.setAnimation(R.raw.snow)
                binding.lottieView.playAnimation()
            }
            weather.contains("rain") -> {
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieView.setAnimation(R.raw.rain)
                binding.lottieView.playAnimation()
            }
            else -> {
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieView.setAnimation(R.raw.sun)
                binding.lottieView.playAnimation()
            }
        }
    }

//    private fun changeImagsaccordingtoWeatherCondition(condition: String){
//        when (condition){
//            "Clear Sky" , "Sunny" , "Clear" -> {
//                binding.root.setBackgroundResource(R.drawable.sunny_background)
//                binding.lottieView.setAnimation(R.raw.sun)
//            }
//
//            "Partly Clouds", "Clouds", "Overcast", "Mist", "Foggy" -> {
//                binding.root.setBackgroundResource(R.drawable.colud_background)
//                binding.lottieView.setAnimation(R.raw.cloud)
//            }
//
//            "Light Rain", "Drizzle", "Moderate rain", "Showers", "Heavy Rain" -> {
//                binding.root.setBackgroundResource(R.drawable.rain_background)
//                binding.lottieView.setAnimation(R.raw.rain)
//            }
//
//            "Light Snow", "Moderate Rain", "Heavy Snow", "Blizzard" -> {
//                binding.root.setBackgroundResource(R.drawable.snow_background)
//                binding.lottieView.setAnimation(R.raw.snow)
//            }
//
//            else -> {
//                binding.root.setBackgroundResource(R.drawable.sunny_background)
//                binding.lottieView.setAnimation(R.raw.sun)
//            }
//
//        }
//        binding.lottieView.playAnimation()
//    }

}

    private fun date(): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun time(timestamp: Long): String{
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format((Date(timestamp*1000)))
    }

    private fun dayName(timestamp: Long): String {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

