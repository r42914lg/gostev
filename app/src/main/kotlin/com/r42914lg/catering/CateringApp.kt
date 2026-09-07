package com.r42914lg.catering

import android.app.Application
import android.content.Context
import com.r42914lg.catering.core.data.DataInitializer
import com.r42914lg.catering.core.prefs.PrefsInitializer
import com.r42914lg.catering.remoteconfig.RemoteConfigInitializer
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.loadKoinModules
import org.koin.core.context.startKoin

interface CoreApp {
    fun getApplicationContext(): Context
}

class CateringApp : Application(), CoreApp {
    override fun onCreate() {
        super.onCreate()
        buildGraph()
    }

    private fun buildGraph() {
        startKoin {
            androidContext(getApplicationContext())
            loadKoinModules(
                listOf(
                    PrefsInitializer().module,
                    RemoteConfigInitializer().module,
                    DataInitializer().module,
                    AppInitializer().module
                )
            )
        }
    }
}

