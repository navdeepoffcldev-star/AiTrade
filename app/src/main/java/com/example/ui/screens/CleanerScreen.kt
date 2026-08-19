package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ui.components.LiquidSubScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CleanerScreen(navController: NavController) {
    var isCleaning by remember { mutableStateOf(false) }
    var cleaned by remember { mutableStateOf(false) }
    val rotation = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()

    LiquidSubScreen(
        title = "Deep Cleaner",
        onBack = { navController.popBackStack() }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(200.dp)
            ) {
                CircularProgressIndicator(
                    progress = { if (cleaned) 1f else if (!isCleaning) 0.8f else rotation.value / 360f },
                    modifier = Modifier
                        .fillMaxSize()
                        .rotate(rotation.value),
                    color = Color(0xFFFF3D00),
                    trackColor = Color.White.copy(alpha = 0.1f),
                    strokeWidth = 12.dp
                )
                Icon(
                    imageVector = Icons.Default.CleaningServices,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(64.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = if (cleaned) "0 MB Junk" else if (isCleaning) "Scanning..." else "1.2 GB Junk Found",
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Cache, Temp files, and obsolete APKs",
                color = Color.White.copy(alpha = 0.6f),
                style = MaterialTheme.typography.bodyLarge
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            Button(
                onClick = {
                    if (!isCleaning && !cleaned) {
                        isCleaning = true
                        coroutineScope.launch {
                            rotation.animateTo(
                                targetValue = 360f * 3,
                                animationSpec = tween(durationMillis = 2000, easing = LinearEasing)
                            )
                            isCleaning = false
                            cleaned = true
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (cleaned) Color(0xFF00E676) else Color(0xFFFF3D00)
                ),
                enabled = !isCleaning && !cleaned
            ) {
                Text(
                    text = if (cleaned) "Cleaned" else if (isCleaning) "Cleaning..." else "Clean Now",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}
