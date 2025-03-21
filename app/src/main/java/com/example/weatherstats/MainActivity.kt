package com.example.weatherstats

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.weatherstats.api.WeatherService
import com.example.weatherstats.models.WeatherResponse
import com.example.weatherstats.ui.theme.WeatherStatsTheme
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import androidx.compose.material3.Text
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            WeatherStatsTheme {
                //WeatherScreen()
                WeatherApp()
            }
        }
    }

}

@Composable
fun WeatherApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "homeScreen"
    ) {
        composable("homeScreen") {
            HomeScreen(navController)
        }

        composable("weatherScreen") {
            WeatherScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(navController: NavController? = null) {
    var city by remember { mutableStateOf("") }
    var weatherResponse by remember { mutableStateOf<WeatherResponse?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Weather Stats") },
                navigationIcon = {
                    navController?.let {
                        IconButton(onClick = { it.navigateUp() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)
        ) {
            TextField(
                value = city,
                onValueChange = { city = it },
                label = { Text("Enter City Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    if (city.isNotBlank()) {
                        fetchWeather(city) { response, error ->
                            weatherResponse = response
                            errorMessage = error
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Get Weather")
            }

            Spacer(modifier = Modifier.height(16.dp))

            when {
                weatherResponse != null -> WeatherCard(weatherResponse!!)
                errorMessage != null -> Text("Error: $errorMessage", color = MaterialTheme.colorScheme.error)
                else -> Text("Enter a city and tap 'Get Weather'")
            }
        }
    }
}

//Weather information display:
@Composable
fun WeatherCard(weather: WeatherResponse) {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            val locationName = weather.location.name
            val countryName = weather.location.country

            Text("Location: $locationName, $countryName")
            Text("Temperature: ${weather.current.temperature}°C")
            Text("Condition: ${weather.current.weather_descriptions.firstOrNull() ?: "Unknown"}")
        }
    }
}

fun fetchWeather(location: String, onResult: (WeatherResponse?, String?) -> Unit) {
    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.weatherstack.com/")
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
