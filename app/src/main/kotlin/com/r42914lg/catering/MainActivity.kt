package com.r42914lg.catering

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.r42914lg.catering.ui.mvi.MainStateHolder
import com.r42914lg.catering.ui.theme.CateringTheme
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val mainStateHolder: MainStateHolder by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CateringTheme {
                Text("hello")
            }
        }
    }
}