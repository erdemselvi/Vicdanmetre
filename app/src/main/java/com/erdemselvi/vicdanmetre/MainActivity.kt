package com.erdemselvi.vicdanmetre

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.erdemselvi.vicdanmetre.screens.HomeScreen
import com.erdemselvi.vicdanmetre.screens.ProfileScreen
import com.erdemselvi.vicdanmetre.screens.ScenarioScreen
import com.erdemselvi.vicdanmetre.screens.SplashScreen
import com.erdemselvi.vicdanmetre.theme.VicdanimTheme
import com.erdemselvi.vicdanmetre.utils.PermissionManager
import com.erdemselvi.vicdanmetre.utils.VicdanimNotificationManager
import com.erdemselvi.vicdanmetre.screens.*
import com.erdemselvi.vicdanmetre.theme.VicdanimTheme
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : ComponentActivity() {
    @SuppressLint("MissingPermission")
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Bildirim izni verildi
            VicdanimNotificationManager(this).sendDailyReminder()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // FCM Token'ı al
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d("FCM_TOKEN", token)
                // Token'ı backend'e gönder
            }
        }
        // İzin kontrolü
        val permissionManager = PermissionManager(this)
        if (!permissionManager.hasNotificationPermission()) {
            permissionManager.requestNotificationPermission(requestPermissionLauncher)
        }

        setContent {
            VicdanimTheme {
                VicdanimApp()
            }
        }
    }
}

@Composable
fun VicdanimApp() {
    val navController = rememberNavController()

    Scaffold { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "splash",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("splash") { SplashScreen(navController) }
            composable("home") { HomeScreen(navController) }
            // Scenario Screen with parameter
            composable(
                route = "scenario/{scenarioId}",
                arguments = listOf(
                    navArgument("scenarioId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                ScenarioScreen(
                    navController = navController,
                    scenarioId = backStackEntry.arguments?.getString("scenarioId")
                )
            }
            // Scenario Complete Screen - YENİ EKLENEN!
            composable(
                route = "scenario_complete/{scenarioId}",
                arguments = listOf(
                    navArgument("scenarioId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                ScenarioCompleteScreen(
                    navController = navController,
                    scenarioId = backStackEntry.arguments?.getString("scenarioId") ?: ""
                )
            }
            composable("profile") { ProfileScreen(navController) }
            composable("journal") { JournalScreen(navController) }
            composable("badges") { BadgesScreen(navController) }
            composable("leaderboard") { LeaderboardScreen(navController) }
            // Scenarios List Screen - YENİ EKLENEN!
            composable("scenarios_list") {
                ScenariosListScreen(navController)
            }
        }
    }
}