package com.example.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.*

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "dashboard") {
        composable("dashboard") { DashboardScreen(navController) }
        composable("file_manager") { FileManagerScreen(navController) }
        composable("security") { SecurityScreen(navController) }
        composable("cleaner") { CleanerScreen(navController) }
        composable("drive") { DriveScreen(navController) }
        composable("storage_health") { StorageHealthScreen(navController) }
        composable("ram_swapper") { RamSwapperScreen(navController) }
    }
}
