package com.example.weatherstats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun HomeScreen(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF87CEEB)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "Welcome to Weather Stats",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Get real-time weather details for any city.",
                fontSize = 16.sp,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { navController.navigate("weatherScreen") },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(Color.White),
                modifier = Modifier
                    .height(50.dp)
                    .width(200.dp)
            ) {
                Text(
                    text = "Enter City",
                    fontSize = 18.sp,
                    color = Color(0xFF87CEEB)
                )
            }
        }
    }
}
