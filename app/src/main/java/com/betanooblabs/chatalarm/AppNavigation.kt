package com.betanooblabs.chatalarm

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.betanooblabs.chatalarm.settings.AlarmContactsScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                onAlarmContactsClick = {
                    navController.navigate("alarm_contacts")
                }
            )
        }

        composable("alarm_contacts") {
            AlarmContactsScreen()
        }
    }
}