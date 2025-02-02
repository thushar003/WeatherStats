package com.example.weatherstats

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.weatherstats.api.WeatherService
import com.example.weatherstats.models.WeatherResponse
import com.example.weatherstats.ui.theme.WeatherStatsTheme
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Text

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //fetchWeather("New York")
        enableEdgeToEdge()
        setContent {
            WeatherStatsTheme {
                WeatherScreen()
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen() {
    var weatherResponse by remember { mutableStateOf<WeatherResponse?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        fetchWeather("New York") { response, error ->
            weatherResponse = response
            errorMessage = error
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Weather Stats") })
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (weatherResponse != null) {
                WeatherCard(weatherResponse!!)
            } else if (errorMessage != null) {
                Text("Error: $errorMessage", color = MaterialTheme.colorScheme.error)
            } else {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            }
        }
    }
}

@Composable
fun WeatherCard(weather: WeatherResponse) {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            val locationName = weather.location?.name ?: "Unknown Location"
            val countryName = weather.location?.country ?: "Unknown Country"

            Text("Location: $locationName, $countryName")
            Text("Temperature: ${weather.current.temperature}°C")
            Text("Condition: ${weather.current.weather_descriptions.firstOrNull() ?: "Unknown"}")
        }
    }
}

fun fetchWeather(location: String, onResult: (WeatherResponse?, String?) -> Unit) {
    val retrofit = Retrofit.Builder()
        .baseUrl("http://api.weatherstack.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service = retrofit.create(WeatherService::class.java)

    val call = service.getCurrentWeather("83e378c314cd8046ecf2bdd648e7e2e4", location)
    call.enqueue(object : Callback<WeatherResponse> {
        override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
            if (response.isSuccessful) {
                onResult(response.body(), null)
            } else {
                onResult(null, "API Error: ${response.message()}")
            }
        }

        override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
            onResult(null, "Network Error: ${t.message}")
        }
    })
}
