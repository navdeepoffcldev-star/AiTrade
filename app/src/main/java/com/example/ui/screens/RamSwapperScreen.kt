package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ui.components.LiquidSubScreen
import com.example.ui.components.LiquidCard

@Composable
fun RamSwapperScreen(navController: NavController) {
    var virtualRam by remember { mutableFloatStateOf(2f) }

    LiquidSubScreen(
        title = "RAM Swapper",
        onBack = { navController.popBackStack() }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Memory,
                contentDescription = null,
                tint = Color(0xFFFFC400),
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Virtual RAM Extension",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Use internal storage to expand memory",
                color = Color.White.copy(alpha = 0.6f),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(48.dp))

            LiquidCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "+${virtualRam.toInt()} GB",
                        color = Color(0xFFFFC400),
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Slider(
                        value = virtualRam,
                        onValueChange = { virtualRam = it },
                        valueRange = 0f..8f,
                        steps = 7,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFFFFC400),
                            activeTrackColor = Color(0xFFFFC400),
                            inactiveTrackColor = Color.White.copy(alpha = 0.1f)
                        )
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("0 GB", color = Color.White.copy(alpha = 0.5f))
                        Text("8 GB", color = Color.White.copy(alpha = 0.5f))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC400))
            ) {
                Text("Apply Changes & Reboot", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}
