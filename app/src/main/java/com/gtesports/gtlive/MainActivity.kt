package com.gtesports.gtlive

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gtesports.gtlive.ui.screens.AnalyticsScreen
import com.gtesports.gtlive.ui.screens.CameraLiveScreen
import com.gtesports.gtlive.ui.screens.DashboardScreen
import com.gtesports.gtlive.ui.screens.GoLiveScreen
import com.gtesports.gtlive.ui.screens.LoginScreen
import com.gtesports.gtlive.ui.screens.SceneEditorScreen
import com.gtesports.gtlive.ui.screens.ScreenLiveScreen
import com.gtesports.gtlive.ui.screens.SettingsScreen
import com.gtesports.gtlive.ui.screens.SplashScreen
import com.gtesports.gtlive.ui.screens.StreamManagerScreen
import com.gtesports.gtlive.ui.theme.GTBackgroundBlack
import com.gtesports.gtlive.ui.theme.GTLiveTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GTLiveTheme {
                GTLiveAppNavigation()
            }
        }
    }
}

@Composable
fun GTLiveAppNavigation() {
    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(GTBackgroundBlack)
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "splash",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("splash") {
                SplashScreen(onFinishSplash = { navController.navigate("login") })
            }
            composable("login") {
                LoginScreen(onLoginSuccess = { navController.navigate("dashboard") })
            }
            composable("dashboard") {
                DashboardScreen(navController = navController)
            }
            composable("camera_live") {
                CameraLiveScreen(navController = navController)
            }
            composable("screen_live") {
                ScreenLiveScreen(navController = navController)
            }
            composable("scene_editor") {
                SceneEditorScreen(navController = navController)
            }
            composable("stream_manager") {
                StreamManagerScreen(navController = navController)
            }
            composable("go_live") {
                GoLiveScreen(navController = navController)
            }
            composable("analytics") {
                AnalyticsScreen(navController = navController)
            }
            composable("settings") {
                SettingsScreen(navController = navController)
            }
        }
    }
}
