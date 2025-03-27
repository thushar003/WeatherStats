package com.example.weatherstats

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Bedtime

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            WeatherStatsTheme {
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
        colors = CardDefaults.cardColors(containerColor = Color(0xFF90CAF9)), // Light blue background
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${weather.location.name}, ${weather.location.country}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
//                Image(
//                    painter = painterResource(id = R.drawable.weather_icon), // Replace with dynamic icon
//                    contentDescription = "Weather Icon",
//                    modifier = Modifier.size(48.dp)
//                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${weather.current.temperature}°C",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Text(
                text = weather.current.weather_descriptions.firstOrNull() ?: "Unknown",
                fontSize = 16.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.WbSunny, contentDescription = "Sunrise", tint = Color.Yellow)
                    Text(text = "Sunrise", color = Color.White, fontSize = 14.sp)
                    Text(text = weather.current.astro.sunrise, color = Color.White, fontSize = 14.sp)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.NightlightRound, contentDescription = "Sunset", tint = Color.Yellow)
                    Text(text = "Sunset", color = Color.White, fontSize = 14.sp)
                    Text(text = weather.current.astro.sunset, color = Color.White, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.NightsStay, contentDescription = "Moonrise", tint = Color.White)
                    Text(text = "Moonrise", color = Color.White, fontSize = 14.sp)
                    Text(text = weather.current.astro.moonrise, color = Color.White, fontSize = 14.sp)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Bedtime, contentDescription = "Moonset", tint = Color.White)
                    Text(text = "Moonset", color = Color.White, fontSize = 14.sp)
                    Text(text = weather.current.astro.moonset, color = Color.White, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Moon Phase: ${weather.current.astro.moon_phase}",
                fontSize = 16.sp,
                color = Color.White
            )
            Text(
                text = "Illumination: ${weather.current.astro.moon_illumination}%",
                fontSize = 16.sp,
                color = Color.White
            )
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
