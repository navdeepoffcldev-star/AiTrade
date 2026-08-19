package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ui.components.LiquidCard
import com.example.ui.components.LiquidSubScreen

@Composable
fun SecurityScreen(navController: NavController) {
    var pin by remember { mutableStateOf("") }
    var unlocked by remember { mutableStateOf(false) }

    LiquidSubScreen(
        title = "SSD Security Vault",
        onBack = { navController.popBackStack() }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = if (unlocked) Icons.Default.LockOpen else Icons.Default.Lock,
                contentDescription = null,
                tint = if (unlocked) Color(0xFF00E676) else Color(0xFFFF3D00),
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = if (unlocked) "SSD Unlocked" else "Enter Password to Unlock SSD",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                for (i in 0 until 4) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (i < pin.length) Color(0xFF00E5FF) else Color.White.copy(alpha = 0.2f),
                            modifier = Modifier.fillMaxSize()
                        ) {}
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            if (!unlocked) {
                // Number Pad
                val buttons = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                    listOf("", "0", "C")
                )

                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    for (row in buttons) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            for (btn in row) {
                                if (btn.isEmpty()) {
                                    Spacer(modifier = Modifier.weight(1f))
                                } else {
                                    LiquidCard(
                                        modifier = Modifier
                                            .weight(1f)
                                            .aspectRatio(1f),
                                        onClick = {
                                            if (btn == "C") {
                                                if (pin.isNotEmpty()) pin = pin.dropLast(1)
                                            } else {
                                                if (pin.length < 4) pin += btn
                                                if (pin.length == 4) {
                                                    unlocked = true
                                                }
                                            }
                                        }
                                    ) {
                                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                            Text(
                                                text = btn,
                                                color = Color.White,
                                                style = MaterialTheme.typography.headlineMedium
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                Text(
                    text = "Access Granted. All encrypted files are now readable.",
                    color = Color.White.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = { 
                        unlocked = false
                        pin = "" 
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF3D00))
                ) {
                    Text("Lock SSD Now", color = Color.White)
                }
            }
        }
    }
}
