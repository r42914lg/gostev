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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.r42914lg.catering.designsys.CateringTheme
import com.r42914lg.catering.remoteconfig.RemoteConfig
import com.r42914lg.catering.ui.AnimatedSplashScreen
import com.r42914lg.catering.ui.CalendarScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val remoteConfig: RemoteConfig by inject()

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

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                remoteConfig.fetch()
                delay(RemoteConfig.RC_SECOND_ATTEMPT_TIMEOUT)
                while (isActive) {
                    remoteConfig.fetch()
                    delay(RemoteConfig.RC_TTL)
                }
            }
        }
    }
}