package com.r42914lg.catering

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.r42914lg.catering.theme.CateringTheme
import com.r42914lg.catering.ui.AnimatedSplashScreen
import com.r42914lg.catering.ui.CalendarScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CateringTheme {
                var showSplash by remember { mutableStateOf(true) }
                if (showSplash) {
                    AnimatedSplashScreen(onAnimationFinished = { showSplash = false })
                } else {
                    CalendarScreen()
                }
            }
        }
    }
}